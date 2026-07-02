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
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoPublisher {
    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Config.class, new Config.Deserializer())
            .registerTypeAdapter(ModInfo.class, new ModInfo.Deserializer())
            .registerTypeAdapter(AuthorInfo.class, new AuthorInfo.Deserializer())
            .registerTypeAdapter(ChangelogInfo.class, new ChangelogInfo.Deserializer())
            .registerTypeAdapter(DependencyInfo.class, new DependencyInfo.Deserializer())
            .registerTypeAdapter(DependencyType.class, new DependencyType.Deserializer())
            .registerTypeAdapter(AssetsInfo.class, new AssetsInfo.Deserializer())
            .create();
    static final Logger LOGGER = LogUtils.getLogger();

    private static final File CONFIG = new File("build/resources/main/publish_config.json");
    static final String SOURCE_PATH = "build/libs";
    private static final File DATA_CACHE = new File("run/PublishCache.txt");
    static final String CHANGELOG_PATH = "publish/changelog.txt";
    static final String CATEGORIES_PATH = "publish/categories.json";

    record Config(AuthorInfo authorInfo,
                  ModInfo modInfo,
                  String mcVersion,
                  String loaderVersion,
                  String modrinthId,
                  String curseforgeId,
                  String[] modules,
                  boolean withSources,
                  DependencyInfo[] dependencies,
                  ChangelogInfo changelogInfo,
                  AssetsInfo assetsInfo
    ) {
        private static class Deserializer implements JsonDeserializer<Config> {

            @Override
            public Config deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                AuthorInfo authorInfo = jsonDeserializationContext.deserialize(object.get("author"), AuthorInfo.class);
                ModInfo modInfo = jsonDeserializationContext.deserialize(object.get("mod"), ModInfo.class);
                String mcVersion = GsonHelper.getAsString(object, "mc_version");
                String loaderVersion = GsonHelper.getAsString(object, "loader_version");
                String modrinthId = GsonHelper.getOptionalAsString(object, "modrinth_id");
                String curseforgeId = GsonHelper.getOptionalAsString(object, "curseforge_id");
                String[] modules = object.has("modules") ? jsonDeserializationContext.deserialize(object.get("modules"), String[].class) : new String[0];
                boolean withSources = GsonHelper.getOptionalAsBoolean(object, "with_sources", false);
                DependencyInfo[] infos = jsonDeserializationContext.deserialize(object.get("dependencies"), DependencyInfo[].class);
                ChangelogInfo changelogInfo = object.has("changelog") ? jsonDeserializationContext.deserialize(object.get("changelog"), ChangelogInfo.class) : ChangelogInfo.DEFAULT;
                AssetsInfo assetsInfo = object.has("assets") ? jsonDeserializationContext.deserialize(object.get("assets"), AssetsInfo.class) : AssetsInfo.DEFAULT;
                return new Config(authorInfo, modInfo, mcVersion, loaderVersion, modrinthId, curseforgeId, modules, withSources, infos, changelogInfo, assetsInfo);
            }
        }
    }

    record ModInfo(String id, String name, String version, String artifactVersion) {

        private static class Deserializer implements JsonDeserializer<ModInfo> {

            @Override
            public ModInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                String id = GsonHelper.getAsString(object, "id");
                String name = GsonHelper.getAsString(object, "name");
                String version = GsonHelper.getAsString(object, "version");
                String artifactVersion = GsonHelper.getAsString(object, "artifact_version");
                return new ModInfo(id, name, version, artifactVersion);
            }
        }
    }

    record AuthorInfo(String email, String name) {

        private static class Deserializer implements JsonDeserializer<AuthorInfo> {

            @Override
            public AuthorInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("author config must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                String email = GsonHelper.getAsString(object, "email");
                String name = GsonHelper.getAsString(object, "name");
                return new AuthorInfo(email, name);
            }
        }
    }

    record ChangelogInfo(String format, String style) {

        public static final ChangelogInfo DEFAULT = new ChangelogInfo("html", "list");

        private Changelog createLog(String categoriesLocation) {
            return switch (style) {
                case "list" -> {
                    ListChangelog.gatherCategories(categoriesLocation);
                    yield new ListChangelog();
                }
                case "none" -> new PlainChangelog();
                default -> throw new IllegalArgumentException("unknown style: " + style);
            };
        }

        private String compileLog(Changelog log) {
            return switch (format) {
                case "html" -> log.toHtml();
                case "md" -> log.toMd();
                case "plain" -> log.toPlainText();
                default -> throw new IllegalArgumentException("unknown format: " + format);
            };
        }

        private static class Deserializer implements JsonDeserializer<ChangelogInfo> {

            @Override
            public ChangelogInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("changelog config must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                String format = GsonHelper.getOptionalAsString(object, "format", "html");
                String style = GsonHelper.getOptionalAsString(object, "style", "list");
                return new ChangelogInfo(format, style);
            }
        }
    }

    record DependencyInfo(String modrinthId, String curseforgeId, String versionName, DependencyType type,
                          int ordinal) {

        JsonObject toModrinthDependency(String gameVersion) throws IOException {
            JsonObject object = new JsonObject();
            object.addProperty("version_id", ModrinthPublish.getDependencyVersionId(modrinthId, gameVersion, versionName, ordinal));
            object.addProperty("project_id", modrinthId);
            object.addProperty("dependency_type", type.modrinthId());
            return object;
        }

        public JsonObject toCurseforgeDependency() {
            JsonObject object = new JsonObject();
            object.addProperty("slug", curseforgeId);
            object.addProperty("type", this.type.curseforgeId());
            return object;
        }

        private static class Deserializer implements JsonDeserializer<DependencyInfo> {

            @Override
            public DependencyInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("dependency info must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                String modrinthId = GsonHelper.getOptionalAsString(object, "modrinth_id");
                String curseforgeId = GsonHelper.getOptionalAsString(object, "curseforge_id");
                String versionName = GsonHelper.getAsString(object, "version_name");
                DependencyType depType = jsonDeserializationContext.deserialize(object.get("type"), DependencyType.class);
                int ordinal = GsonHelper.getAsInt(object, "ordinal");
                return new DependencyInfo(modrinthId, versionName, curseforgeId, depType, ordinal);
            }
        }
    }

    enum DependencyType {
        REQUIRED("requiredDependency"),
        OPTIONAL("optionalDependency"),
        INCOMPATIBLE("incompatible"),
        EMBEDDED("embeddedLibrary");

        private final String curseforgeId;

        DependencyType(String curseforgeId) {
            this.curseforgeId = curseforgeId;
        }

        public String modrinthId() {
            return this.name().toLowerCase();
        }

        public String curseforgeId() {
            return curseforgeId;
        }

        private static class Deserializer implements JsonDeserializer<DependencyType> {

            @Override
            public DependencyType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString())
                    throw new JsonParseException("dependency type must be string");
                try {
                    return DependencyType.valueOf(json.getAsJsonPrimitive().getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException("unknown dependency type: " + json.getAsJsonPrimitive().getAsString());
                }
            }
        }
    }

    record AssetsInfo(String sourcePath, String changelogPath, String categoriesPath) {
        public static final AssetsInfo DEFAULT = new AssetsInfo(SOURCE_PATH, CHANGELOG_PATH, CATEGORIES_PATH);

        private static class Deserializer implements JsonDeserializer<AssetsInfo> {

            @Override
            public AssetsInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
                JsonObject object = jsonElement.getAsJsonObject();
                String sourcePath = GsonHelper.getOptionalAsString(object, "source", SOURCE_PATH);
                String changelogPath = GsonHelper.getOptionalAsString(object, "changelog", CHANGELOG_PATH);
                String categoriesPath = GsonHelper.getOptionalAsString(object, "categories", CATEGORIES_PATH);
                return new AssetsInfo(sourcePath, changelogPath, categoriesPath);
            }
        }
    }

    private static Config loadConfig() throws FileNotFoundException {
        FileReader reader = new FileReader(CONFIG);
        return GSON.fromJson(reader, Config.class);
    }

    public static void main(String[] args) {
        Config config;
        try {
            config = loadConfig();
        } catch (FileNotFoundException e) {
            LOGGER.error("Config not found.");
            return;
        }
        String modVersion = config.modInfo.version;
        if (DATA_CACHE.exists()) {
            try {
                String lastVersion = new String(Files.readAllBytes(DATA_CACHE.toPath()));
                if (lastVersion.equals(modVersion)) {
                    LOGGER.error("last published version matches current version"); //do not upload the same version twice
                    return;
                }
            } catch (IOException e) {
                LOGGER.warn("unable to read version cache: {}, ignoring", e.getMessage());
            }
        }

        LOGGER.debug("Verifying file existence...");
        List<Source> source = verifyFileExistence(config.modInfo.id, config.modInfo.artifactVersion, config.assetsInfo.sourcePath, config.modules, config.withSources);
        LOGGER.debug("successfully verified {} files", source.size());

        LOGGER.debug("Compiling Changelog...");
        try {
            createChangelog(config.changelogInfo == null ? ChangelogInfo.DEFAULT : config.changelogInfo, config.assetsInfo.changelogPath, config.assetsInfo.categoriesPath);
        } catch (FileNotFoundException e) {
            LOGGER.error("changelog not found: {}", e.getMessage());
            return;
        }

        String mcVersion = config.mcVersion;
        String fmlVersion = config.loaderVersion;
        LOGGER.info("Auto Publish activated with args:");
        LOGGER.info("modId=\"{}\", modName=\"{}\", modVersion={}, mcVersion={}, loaderVersion={}", config.modInfo.id, config.modInfo.name, modVersion, mcVersion, fmlVersion);

        try {
            try (HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()) {
                if (
                    //  (config.curseforgeId == null || CurseforgePublish.publish(config, client, source)) &&
                        (config.modrinthId == null || ModrinthPublish.publish(config, client, source))) {
                    saveDataCache(modVersion);
                    clearChangelog();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error accessing API:");
            e.printStackTrace(System.err);
        }
    }

    //region file verify
    record Source(String moduleName, File obj) {
    }

    /**
     * returns a list of all files, including main, module and source if enabled
     */
    private static List<Source> verifyFileExistence(String modId, String artifactVersion, String sourcePath, String[] modules, boolean withSources) {
        List<Source> sources = new ArrayList<>();
        String fileBase = String.format("%s/%s-", sourcePath, modId) + artifactVersion;
        sources.add(new Source("primary", checkFileExistence(fileBase)));
        if (withSources)
            sources.add(new Source("primary-sources", checkFileExistence(fileBase + "-sources")));
        for (String module : modules) {
            sources.add(new Source(module, checkFileExistence(fileBase + "-" + module)));
            if (withSources)
                sources.add(new Source(module + "-sources", checkFileExistence(String.format("%s-%s-sources", fileBase, module))));
        }

        return sources;
    }

    private static File checkFileExistence(String fileBase) {
        File file = new File(fileBase + ".jar");
        if (!file.exists()) throw new NullPointerException("missing jar at " + file.getPath());
        return file;
    }
    //endregion

    private static void saveDataCache(String modVersion) throws IOException {
        FileWriter writer = new FileWriter(DATA_CACHE);
        writer.write(modVersion);
        writer.close();
    }

    private static void clearChangelog() throws IOException {
        FileWriter writer = new FileWriter(CHANGELOG_PATH);
        writer.close();
    }

    @Deprecated
    static String formatVersion(String modVersion, String mcVersion) {
        return String.format("v%s-mc%s", modVersion, mcVersion);
    }

    static String getAuth(boolean modrinth) {
        return modrinth ? System.getProperty("modrinthAuth") : System.getProperty("curseforgeAuth");
    }

    //region changelog
    private static String changelog;

    static void createChangelog(ChangelogInfo info, String changelogLocation, String categoriesLocation) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(changelogLocation));
        Changelog log = info.createLog(categoriesLocation);
        log.parse(reader);
        changelog = info.compileLog(log);
    }

    static String getChangelog() {
        return changelog;
    }

    private interface Changelog {

        String toHtml();

        String toMd();

        String toPlainText();

        void parse(BufferedReader reader);
    }

    private static class ListChangelog implements Changelog {
        private static List<Category> categories;

        private static void gatherCategories(String categoriesPath) {
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

        private ListChangelog() {
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

    private static final class PlainChangelog implements Changelog {
        private String content;

        @Override
        public String toHtml() {
            return content;
        }

        @Override
        public String toMd() {
            return content;
        }

        @Override
        public String toPlainText() {
            return content;
        }

        @Override
        public void parse(BufferedReader reader) {
            content = reader.lines().collect(Collectors.joining("\n")); //add HTML / MD line feed character
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (PlainChangelog) obj;
            return Objects.equals(this.content, that.content);
        }

        @Override
        public int hashCode() {
            return content.hashCode();
        }

        @Override
        public String toString() {
            return "PlainChangelog[" +
                    "content=" + content + ']';
        }

    }
    //endregion

    private static final List<String> DEPENDENCY_TYPES = List.of("required", "optional", "incompatible", "embedded");

    static boolean verifyDependencyType(Object type) {
        return type instanceof String s && DEPENDENCY_TYPES.contains(s);
    }

    static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] arr : arrays) length += arr.length;
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }
        return result;
    }
}
