package net.kapitencraft.kap_lib.publish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.GsonHelper;
import org.checkerframework.checker.units.qual.C;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AutoPublisher {
    private static final Gson GSON = new GsonBuilder().create();

    private static final File CONFIG = new File("run/PublishConfig.json");
    private static final File AUTHENTICATION = new File("run/AuthCache.txt");
    private static final File DATA_CACHE = new File("run/PublishCache.txt");
    private static final File CHANGE_LOG = new File("CHANGELOG.txt");
    private static final String API_URL = "https://api.modrinth.com/v2/version";

    private record Config(String email, String author, String projectId, boolean sourcesFile) {

    }

    private static Config loadConfig() throws FileNotFoundException {
        FileReader reader = new FileReader(CONFIG);
        JsonObject object = GSON.fromJson(reader, JsonObject.class);
        return new Config(
                object.getAsJsonPrimitive("author_email").getAsString(),
                object.getAsJsonPrimitive("author").getAsString(),
                object.getAsJsonPrimitive("project_id").getAsString(),
                object.getAsJsonPrimitive("with_sources").getAsBoolean()
        );
    }


    public static void main(String[] args) {
        String modId = args[0];
        String modName = args[1];
        String modVersion = args[2];
        String mcVersion = args[3];
        String fmlVersion = args[4];
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
            URL url = new URL(API_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            String boundary = "----Boundary" + UUID.randomUUID();

            Config config = loadConfig();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", String.format(config.author + "/%s/%s (%s)", modName, modVersion, config.email));
            connection.setRequestProperty("Authorization", new String(Files.readAllBytes(AUTHENTICATION.toPath())));

            String fileBase = String.format("./build/libs/%s-", modId) + formatVersion(modVersion, mcVersion, fmlVersion);

            File mainFile = new File(fileBase + ".jar");
            String mainHash = getFileSHA512(mainFile);



            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

                // Add text part
                addData(writer, boundary, modId, modName, modVersion, mcVersion, fmlVersion, config.projectId);

                // Add file part
                addFilePart(writer, outputStream, boundary, "primary", mainFile);

                if (config.sourcesFile) {
                    File sourcesFile = new File(fileBase + "-sources.jar");
                    String sourcesHash = getFileSHA512(sourcesFile);
                    addFilePart(writer, outputStream, boundary, "sources", sourcesFile);
                }
                // Write the final boundary directly to OutputStream
                outputStream.write(("--" + boundary + "--\r\n").getBytes());
                outputStream.flush();
            }

            //response
            int response = connection.getResponseCode();

            InputStream dataStream;
            if (response != HttpsURLConnection.HTTP_OK) {
                System.err.println("failed: " + response);
                dataStream = connection.getErrorStream();
            } else {
                clearChangelog();
                saveDataCache(modVersion);
                dataStream = connection.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(dataStream));

            JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

            Map<String, Object> data = GSON.fromJson(reader, Map.class);

            reader.close();

            System.out.println("Successfully created new version with id '" + data.get("id") + "'");
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

    // Helper method to add a text field
    private static void addData(PrintWriter writer, String boundary, String modId, String modName, String modVersion, String mcVersion, String forgeVersion, String projectId) throws FileNotFoundException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"data\"\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        writer.append(addVersionData(modId, modName, modVersion, mcVersion, forgeVersion, projectId)).append("\r\n");
        writer.flush();
    }

    // Helper method to add a file field
    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                .append("\"; filename=\"").append(file.getName()).append("\"\r\n");
        writer.append("Content-Type: application/java-archive\r\n\r\n");
        writer.flush();

        Files.copy(file.toPath(), outputStream);
        outputStream.flush();
        writer.append("\r\n");
        writer.flush();
    }

    private static String addVersionData(String modId, String modName, String modVersion, String mcVersion, String forgeVersion, String projectId) throws FileNotFoundException {
        Map<String, Object> data = new HashMap<>();

        JsonObject object = new JsonObject();

        data.put("name", String.format("%s v%s", modName, modVersion));
        data.put("version_number", formatVersion(modVersion, mcVersion, forgeVersion));
        data.put("loaders", new String[] {"forge"});
        data.put("game_versions", new String[]{mcVersion});
        data.put("version_type", "release");
        data.put("dependencies", new String[]{});
        data.put("featured", true);
        data.put("status", "listed");
        data.put("project_id", projectId);
        data.put("file_parts", new String[] {"primary", "sources"});
        data.put("primary_file", "primary");
        data.put("changelog", createChangelog());

        return GSON.toJson(data);
    }

    private static String getFileSHA512(File file) {
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

    private static String createChangelog() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(CHANGE_LOG));
        List<String> additions = new ArrayList<>();
        List<String> moves = new ArrayList<>();
        List<String> fixes = new ArrayList<>();
        List<String> removes = new ArrayList<>();
        List<String> uncategorized = new ArrayList<>();
        reader.lines().forEach(s -> {
            if (s.startsWith("added ")) additions.add(s.substring(6));
            else if (s.startsWith("removed ")) removes.add(s.substring(7));
            else if (s.startsWith("moved ")) moves.add(s.substring(6));
            else if (s.startsWith("modified ")) moves.add(s.substring(9));
            else if (s.startsWith("fixed ")) fixes.add(s.substring(6));
            else uncategorized.add(s);
        });
        StringBuilder changeLogBuilder = new StringBuilder();
        addElements(changeLogBuilder, additions, "Added");
        addElements(changeLogBuilder, moves, "Moved");
        addElements(changeLogBuilder, fixes, "Fixed");
        addElements(changeLogBuilder, removes, "Removed");
        addElements(changeLogBuilder, uncategorized, "Uncategorized");
        return changeLogBuilder.toString();
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

    private static String formatVersion(String modVersion, String mcVersion, String fmlVersion) {
        return String.format("v%s-mc%s-FML%s", modVersion, mcVersion, fmlVersion);
    }
}
