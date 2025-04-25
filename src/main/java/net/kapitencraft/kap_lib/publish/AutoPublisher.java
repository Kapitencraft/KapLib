package net.kapitencraft.kap_lib.publish;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.kapitencraft.kap_lib.io.network.ModrinthUtils;
import net.minecraft.util.GsonHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

public class AutoPublisher {
    static final Gson GSON = new GsonBuilder().create();

    private static final File CONFIG = new File("build/resources/main/publish_config.json");
    static final File AUTHENTICATION = new File("run/AuthCache.txt");
    private static final File DATA_CACHE = new File("run/PublishCache.txt");
    static final File CHANGE_LOG = new File("CHANGELOG.txt");

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
        System.out.println("Auto Publish activated with args:");
        System.out.printf("modId=\"%s\", modName=\"%s\", modVersion=%s, mcVersion=%s, fmlVersion=%s", modId, modName, modVersion, mcVersion, fmlVersion);
        System.out.println();

        try {
            if (DATA_CACHE.exists()) {
                String lastVersion = new String(Files.readAllBytes(DATA_CACHE.toPath()));
                if (lastVersion.equals(modVersion)) {
                    System.err.println("last published version matches current version"); //do not upload the same version twice
                    return;
                }
            }
            if (ModrinthPublish.publish(config)) {
                saveDataCache(modVersion);
                clearChangelog();
            }
        } catch (Exception e) {
            System.err.println("Error accessing API:");
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

    private static final List<String> DEPENDENCY_TYPES = List.of("required", "optional", "incompatible", "embedded");

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
        //TODO extend changelog capabilities
        if (changelog == null) {
            BufferedReader reader = new BufferedReader(new FileReader(CHANGE_LOG));
            List<String> additions = new ArrayList<>();
            List<String> moves = new ArrayList<>();
            List<String> fixes = new ArrayList<>();
            List<String> removes = new ArrayList<>();
            List<String> uncategorized = new ArrayList<>();
            List<String> knownErrors = new ArrayList<>();
            reader.lines().forEach(s -> {
                if (s.isEmpty()) return;
                if (s.startsWith("added ")) additions.add(s.substring(6));
                else if (s.startsWith("removed ")) removes.add(s.substring(7));
                else if (s.startsWith("moved ")) moves.add(s.substring(6));
                else if (s.startsWith("modified ")) moves.add(s.substring(9));
                else if (s.startsWith("fixed ")) fixes.add(s.substring(6));
                else if (s.startsWith("known error: ")) knownErrors.add(s.substring(13));
                else uncategorized.add(s);
            });
            StringBuilder changelogBuilder = new StringBuilder();
            addElements(changelogBuilder, additions, "Added");
            addElements(changelogBuilder, moves, "Moved");
            addElements(changelogBuilder, fixes, "Fixed");
            addElements(changelogBuilder, removes, "Removed");
            addElements(changelogBuilder, knownErrors, "Known Errors");
            addElements(changelogBuilder, uncategorized, "Uncategorized");
            changelog = changelogBuilder.toString();
        }
        return changelog;
    }

    private static void addElements(StringBuilder dataSink, List<String> data, String name) {
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

    static String formatVersion(String modVersion, String mcVersion, String fmlVersion) {
        return String.format("v%s-mc%s-FML%s", modVersion, mcVersion, fmlVersion);
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
}
