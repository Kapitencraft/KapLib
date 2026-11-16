package net.kapitencraft.kap_lib.publish.changelog;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListChangelog implements Changelog {
    private static List<Category> categories;

    public static void gatherCategories(String categoriesPath) {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.FIXED);
        categories.add(Category.ADDED);
        categories.add(Category.REMOVED);
        categories.add(Category.MOVED);
        categories.add(Category.KNOWN_ERROR);

        loadCategoryFile(categories, categoriesPath);

        ListChangelog.categories = ImmutableList.copyOf(categories);
    }

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Category.class, new Category.Deserializer()).create();

    private static void loadCategoryFile(List<Category> categories, String categoriesPath) {
        try {
            File file = new File(categoriesPath);
            if (!file.exists() || !file.isFile()) return;
            Category[] categories1 = GSON.fromJson(new JsonReader(new FileReader(file)), Category[].class);
            categories.addAll(Arrays.asList(categories1));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final Multimap<Category, String> content;

    public ListChangelog() {
        this.content = HashMultimap.create();
    }

    public String toHtml() {
        StringBuilder builder = new StringBuilder();
        for (Category category : this.content.keySet()) {
            addElementsHtml(builder, this.content.get(category), category.title);
        }
        return builder.toString();
    }

    public String toMd() {
        StringBuilder builder = new StringBuilder();
        for (Category category : this.content.keySet()) {
            addElementsMd(builder, this.content.get(category), category.title);
        }
        return builder.toString();
    }

    @Override
    public String toPlainText() {
        StringBuilder builder = new StringBuilder();
        for (Category category : this.content.keySet()) {
            addElementsPlainText(builder, this.content.get(category), category.title);
        }
        return builder.toString();
    }

    public void parse(BufferedReader reader) {
        reader.lines().forEach(s -> {
            if (s.isEmpty()) return;
            a:
            {
                for (Category category : categories) {
                    Matcher matcher = category.pattern.matcher(s);
                    if (matcher.find()) {
                        this.add(category, s.substring(matcher.end()));
                        break a;
                    }
                }
                add(Category.UNCATEGORIZED, s);
            }
        });
    }

    private static void addElementsPlainText(StringBuilder builder, Collection<String> data, String title) {
        if (data.isEmpty()) return; //skip not used headers
        builder.append(title);
        for (String addition : data) {
            builder.append(addition);
        }
    }

    private static void addElementsHtml(StringBuilder dataSink, Collection<String> data, String name) {
        if (data.isEmpty()) return; //skip not used headers
        dataSink.append(String.format("<h2>%s</h2><ol>\n", name));
        for (String addition : data) {
            dataSink.append(String.format("\t<li>%s</li>\n", addition));
        }
        dataSink.append("</ol>\n");
    }

    private static void addElementsMd(StringBuilder dataSink, Collection<String> data, String name) {
        if (data.isEmpty()) return; //skip not used headers
        dataSink.append(String.format("# %s\n", name));
        int i = 1;
        for (String addition : data) {
            dataSink.append(String.format("%s. %s\n", i, addition));
            i++;
        }
    }

    public void add(Category category, String name) {
        this.content.put(category, name);
    }

    private record Category(Pattern pattern, String title) {
        public static final Category ADDED = new Category(Pattern.compile("^added"), "Added");
        public static final Category REMOVED = new Category(Pattern.compile("^removed"), "Removed");
        public static final Category MOVED = new Category(Pattern.compile("^(moved)|(modified)"), "Moved");
        public static final Category FIXED = new Category(Pattern.compile("^fixed"), "Fixed");
        public static final Category KNOWN_ERROR = new Category(Pattern.compile("^known error:"), "Known Errors");
        public static final Category UNCATEGORIZED = new Category(Pattern.compile(""), "Uncategorized");

        private static class Deserializer implements JsonDeserializer<Category> {

            @Override
            public Category deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (jsonElement.isJsonObject()) {
                    JsonObject object = (JsonObject) jsonElement;
                    if (!object.has("pattern")) throw new JsonParseException("missing 'pattern' field");
                    if (!object.has("title")) throw new JsonParseException("missing 'title' field");
                    return new Category(Pattern.compile(object.getAsJsonPrimitive("pattern").getAsString()), object.getAsJsonPrimitive("title").getAsString());
                }
                throw new JsonParseException("Category is not object");
            }
        }
    }
}