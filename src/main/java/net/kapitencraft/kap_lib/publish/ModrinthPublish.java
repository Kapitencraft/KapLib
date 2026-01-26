package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.kapitencraft.kap_lib.core.io.ByteAccumulator;
import net.kapitencraft.kap_lib.core.util.ModrinthUtils;
import net.minecraft.util.GsonHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

public class ModrinthPublish {
    private static final String API_URL = "https://api.modrinth.com/v2/version";

    static boolean publish(AutoPublisher.Config config, HttpClient client, List<AutoPublisher.Source> sources) {
        String modName = config.modInfo().name();
        String modVersion = config.modInfo().version();
        String mcVersion = config.mcVersion();
        String loaderVersion = config.loaderVersion();
        try {
            String boundary = "----Boundary" + UUID.randomUUID();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL));
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            builder.header("User-Agent", String.format(config.authorInfo().name() + "/%s/%s (%s)", modName, modVersion, config.authorInfo().email()));
            builder.header("Authorization", AutoPublisher.getAuth(true));

            ByteAccumulator accumulator = new ByteAccumulator();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(accumulator, StandardCharsets.UTF_8), true);
            // Add text part
            addData(writer, boundary, modName, modVersion, mcVersion, loaderVersion, config.modrinthId(), fillModules(config.modules(), config.withSources()), config.dependencies());

            // Add file part
            for (AutoPublisher.Source source : sources) {
                addFilePart(writer, accumulator, boundary, source.moduleName(), source.obj());
            }
            // Write the final boundary directly to OutputStream
            accumulator.write(("--" + boundary + "--\r\n").getBytes());
            accumulator.flush();

            byte[] requestData = accumulator.output();

            HttpResponse<String> response = client.send(builder.POST(HttpRequest.BodyPublishers.ofByteArray(requestData)).build(), HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                System.err.println("failed: " + response);
            }

            Map<String, Object> data = AutoPublisher.GSON.fromJson(response.body(), Map.class);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
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
        writer.append("""
                --%s
                Content-Disposition: form-data; name="data"
                Content-Type: application/json; charset=UTF-8
                """).format(boundary);
        writer.append(addVersionData(modName, modVersion, mcVersion, projectId, dependencies, modules)).append("\r\n");
        writer.flush();
    }

    // Helper method to add a file field
    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        writer.append("""
                --%s
                Content-Disposition: form-data; name="%s"; filename="%s"
                Content-Type: application/java-archive
                """).format(boundary, fieldName, file.getName());
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
