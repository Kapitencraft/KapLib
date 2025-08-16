package net.kapitencraft.kap_lib.publish;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoPublisher {
    static final Gson GSON = new GsonBuilder().create();
    static final Logger LOGGER = LogUtils.getLogger();

    private static final File CONFIG = new File("build/resources/main/publish_config.json");
    static final File AUTHENTICATION = new File("run/AuthCache.txt");
    private static final File DATA_CACHE = new File("run/PublishCache.txt");
    static final File CHANGE_LOG = new File("publish/changelog.txt");
    static final File CATEGORIES = new File("publish/categories.json");

    record Config(String email, String author,
                          String modId, String modName,
                          String modVersion, String mcVersion,
                          String fmlVersion, String projectId,
                          String[] extraFiles,
                          JsonObject[] dependencies
    ) {

    }

    private static Config loadConfig() throws FileNotFoundException {
        FileReader reader = new FileReader(CONFIG);
        JsonObject object = GSON.fromJson(reader, JsonObject.class);
        return new Config(
                object.getAsJsonPrimitive("author_email").getAsString(),
                object.getAsJsonPrimitive("author").getAsString(),
                object.getAsJsonPrimitive("mod_id").getAsString(),
                object.getAsJsonPrimitive("mod_name").getAsString(),
                object.getAsJsonPrimitive("mod_version").getAsString(),
                object.getAsJsonPrimitive("mc_version").getAsString(),
                object.getAsJsonPrimitive("fml_version").getAsString(),
                object.getAsJsonPrimitive("project_id").getAsString(),
                optionalList("extra_files", object).stream().map(JsonElement::getAsString).toArray(String[]::new),
                optionalList("dependencies", object).stream().map(JsonElement::getAsJsonObject).toArray(JsonObject[]::new)
        );
    }

    private static List<JsonElement> optionalList(String name, JsonObject object) {
        return object.has(name) ? object.getAsJsonArray(name).asList() : List.of();
    }

    public static void main(String[] args) {
        Config config;
        try {
            config = loadConfig();
        } catch (FileNotFoundException e) {
            System.out.println("Config not found.");
            return;
        }

        String modId = config.modId;
        String modName = config.modName;
        String modVersion = config.modVersion;
        String mcVersion = config.mcVersion;
        String fmlVersion = config.fmlVersion;
        LOGGER.info("Auto Publish activated with args:");
        LOGGER.info("modId=\"{}\", modName=\"{}\", modVersion={}, mcVersion={}, fmlVersion={}", modId, modName, modVersion, mcVersion, fmlVersion);

        try {
            if (DATA_CACHE.exists()) {
                String lastVersion = new String(Files.readAllBytes(DATA_CACHE.toPath()));
                if (lastVersion.equals(modVersion)) {
                    LOGGER.error("last published version matches current version"); //do not upload the same version twice
                    return;
                }
            }
            if (ModrinthPublish.publish(config)) {
                saveDataCache(modVersion);
                clearChangelog();
            }
        } catch (Exception e) {
            LOGGER.error("Error accessing API:");
            e.printStackTrace(System.err);
        }
    }

    private static void saveDataCache(String modVersion) throws IOException {
        FileWriter writer = new FileWriter(DATA_CACHE);
        writer.write(modVersion);
        writer.close();
    }

    private static void clearChangelog() throws IOException {
        FileWriter writer = new FileWriter(CHANGE_LOG);
        writer.close();
    }

    static String getFileSHA512(File file) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(fileData);
            return Base64.getEncoder().encodeToString(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private static String changelog;

    static String createChangelog() throws FileNotFoundException {
        if (changelog == null) {
            BufferedReader reader = new BufferedReader(new FileReader(CHANGE_LOG));
            Changelog log = new Changelog();
            reader.lines().forEach(log::parse);
            changelog = log.toHtml();
        }
        return changelog;
    }

    static String formatVersion(String modVersion, String mcVersion, String fmlVersion) {
        return String.format("v%s-mc%s-NFML%s", modVersion, mcVersion, fmlVersion);
    }

    static String[] authString;

    static String getAuth(boolean modrinth) {
        if (authString == null) {
            try {
                authString = Files.readString(AUTHENTICATION.toPath()).split("\n");
            } catch (IOException e) {
                throw new IllegalStateException("could not load authentication", e);
            }
        }
        return authString[modrinth ? 0 : 1];
    }

    private static class Changelog {
        private static final List<Category> categories = gatherCategories();

        private static List<Category> gatherCategories() {
            List<Category> categories = new ArrayList<>();
            categories.add(Category.FIXED);
            categories.add(Category.ADDED);
            categories.add(Category.REMOVED);
            categories.add(Category.MOVED);
            categories.add(Category.KNOWN_ERROR);

            loadCategoryFile(categories);

            return ImmutableList.copyOf(categories);
        }

        private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Category.class, new Category.Deserializer()).create();

        private static void loadCategoryFile(List<Category> categories) {
            try {
                if (!CATEGORIES.exists() || !CATEGORIES.isFile()) return;
                Category[] categories1 = GSON.fromJson(new JsonReader(new FileReader(CATEGORIES)), Category[].class);
                categories.addAll(Arrays.asList(categories1));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        private final Multimap<Category, String> content;

        private Changelog() {
            this.content = HashMultimap.create();
        }

        public void add(Category category, String name) {
            this.content.put(category, name);
        }

        public void parse(String s) {
            if (s.isEmpty()) return;
            a: {
                for (Category category : categories) {
                    Matcher matcher = category.pattern.matcher(s);
                    if (matcher.find()) {
                        this.add(category, s.substring(matcher.end()));
                        break a;
                    }
                }
                add(Category.UNCATEGORIZED, s);
            }
        }

        public String toHtml() {
            StringBuilder builder = new StringBuilder();
            for (Category category : this.content.keySet()) {
                addElements(builder, this.content.get(category), category.title);
            }
            return builder.toString();
        }

        private static void addElements(StringBuilder dataSink, Collection<String> data, String name) {
            if (data.isEmpty()) return; //skip not used headers
            dataSink.append("<h2>");
            dataSink.append(name);
            dataSink.append("</h2>");
            dataSink.append("<ol>\n");
            for (String addition : data) {
                dataSink.append("\t<li>");
                dataSink.append(addition);
                dataSink.append("</li>\n");
            }
            dataSink.append("</ol>\n");

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
}
