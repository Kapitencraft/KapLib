package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.kapitencraft.kap_lib.core.util.ModrinthUtils;
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

public class ModrinthPublish {
    private static final String API_URL = "https://api.modrinth.com/v2/version";

    static boolean publish(AutoPublisher.Config config, List<AutoPublisher.Source> sources) {
        String modName = config.modInfo().name();
        String modVersion = config.modInfo().version();
        String mcVersion = config.mcVersion();
        String loaderVersion = config.loaderVersion();
        try {
            URL url = new URL(API_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            String boundary = "----Boundary" + UUID.randomUUID();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", String.format(config.authorInfo().name() + "/%s/%s (%s)", modName, modVersion, config.authorInfo().email()));
            connection.setRequestProperty("Authorization", AutoPublisher.getAuth(true));

            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

                // Add text part
                addData(writer, boundary, modName, modVersion, mcVersion, loaderVersion, config.modrinthId(), fillModules(config.modules(), config.withSources()), config.dependencies());

                // Add file part
                for (AutoPublisher.Source source : sources) {
                    addFilePart(writer, outputStream, boundary, source.moduleName(), source.obj());
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
                dataStream = connection.getInputStream();
            }

            JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

            Map<String, Object> data = AutoPublisher.GSON.fromJson(reader, Map.class);

            reader.close();

            if (response == HttpsURLConnection.HTTP_OK) {
                System.out.println("successfully created new version with id '" + data.get("id") + "'");
                return true;
            } else {
                System.err.println("error: " + data.get("error"));
                System.err.println("description: " + data.get("description"));
            }
        } catch (Exception e) {
            System.err.println("Error accessing API:");
            e.printStackTrace(System.err);
        }
        return false;
    }

    private static String[] fillModules(String[] modules, boolean b) {
        List<String> strings = getExtraModules(modules, b);
        strings.addFirst("primary");
        if (b) strings.add("sources");
        for (String module : modules) {
            strings.add(module);
            if (b)
                strings.add(module + "-sources");
        }
        return strings.toArray(String[]::new);
    }

    private static List<String> getExtraModules(String[] modules, boolean b) {
        List<String> s = new ArrayList<>();
        if (b) s.add("sources");
        for (String module : modules) {
            s.add(module);
            if (b)
                s.add(module + "-sources");
        }
        return s;
    }

    // Helper method to add a text field
    private static void addData(PrintWriter writer, String boundary, String modName, String modVersion, String mcVersion, String loaderVersion, String projectId, String[] modules, AutoPublisher.DependencyInfo[] dependencies) throws IOException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"data\"\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        writer.append(addVersionData(modName, modVersion, mcVersion, projectId, dependencies, modules)).append("\r\n");
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

    private static String addVersionData(String modName, String modVersion, String mcVersion, String projectId, AutoPublisher.DependencyInfo[] dependencies, String[] modules) throws IOException {
        Map<String, Object> data = new HashMap<>();

        data.put("name", String.format("%s v%s", modName, modVersion));
        data.put("version_number", AutoPublisher.formatVersion(modVersion, mcVersion));
        data.put("loaders", new String[]{"neoforge"});
        data.put("game_versions", new String[]{mcVersion});
        data.put("version_type", "release");
        addDependencies(dependencies, data, mcVersion);
        data.put("featured", true);
        data.put("status", "listed");
        data.put("project_id", projectId);

        String[] fileParts = new String[modules.length + 1];
        System.arraycopy(modules, 0, fileParts, 1, modules.length);
        fileParts[0] = "primary";
        data.put("file_parts", fileParts);
        data.put("primary_file", "primary");
        data.put("changelog", AutoPublisher.getChangelog());

        return AutoPublisher.GSON.toJson(data);
    }

    private static void addDependencies(AutoPublisher.DependencyInfo[] dependencies, Map<String, Object> data, String gameVersion) throws IOException {
        List<JsonObject> dependencyData = new ArrayList<>();

        for (AutoPublisher.DependencyInfo object : dependencies) {
            dependencyData.add(object.toModrinthDependency(gameVersion));
        }

        data.put("dependencies", dependencyData);
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

    static String getDependencyVersionId(String modId, String gameVersion, String name, int ordinal) throws IOException {
        try {
            Stream<JsonObject> data = ModrinthUtils.readVersions(modId, gameVersion, "AutoPublisherDependency");
            if (data == null) throw new IllegalStateException("connecting to '" + modId + "' failed");
            JsonObject[] available = data.filter(object -> GsonHelper.getAsString(object, "version_number").equals(name)).toArray(JsonObject[]::new);
            if (ordinal >= available.length || ordinal < 0) {
                throw new IndexOutOfBoundsException(String.format("ordinal %s out of bounds for version count %s", ordinal, available.length));
            }
            return GsonHelper.getAsString(available[ordinal], "id");
        } catch (IOException e) {
            throw new IOException("unable to read dependency: " + e.getMessage());
        }
    }
}
