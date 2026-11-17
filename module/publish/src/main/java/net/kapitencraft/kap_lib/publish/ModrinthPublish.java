package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.core.io.ByteAccumulator;
import net.kapitencraft.kap_lib.core.util.ModrinthUtils;
import net.kapitencraft.kap_lib.publish.depends.DependencyInfo;
import net.kapitencraft.kap_lib.publish.info.ArtifactInfo;
import net.kapitencraft.kap_lib.publish.info.Config;
import net.kapitencraft.kap_lib.publish.info.ModInfo;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class ModrinthPublish {
    private static final String API_URL = "https://api.modrinth.com/v2/version";

    @SuppressWarnings("unchecked")
    static boolean publish(Config config, HttpClient client, List<AutoPublisher.Source> sources) {
        String modName = config.modInfo().name();
        String modVersion = config.modInfo().version();
        String mcVersion = config.mcVersion();
        try {
            String boundary = UUID.randomUUID().toString();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL));
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            builder.header("User-Agent", String.format("%s/%s/%s (%s)", config.authorInfo().name(), modName, modVersion, config.authorInfo().email()));
            builder.header("Authorization", AutoPublisher.getAuth(true));

            ByteAccumulator accumulator = new ByteAccumulator();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(accumulator, StandardCharsets.UTF_8), true);
            // Add text part
            addData(writer, boundary, config.modInfo(), mcVersion, config.environment(), config.modrinthId(), fillModules(config.archives(), config.withSources()), config.dependencies());

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
                System.err.println("failed: " + responseCode);

                if (responseCode > 500) {
                    System.err.println("server side error. try again later!");
                    return false;
                }
            }

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Map<String, Object> data = AutoPublisher.GSON.fromJson(response.body(), Map.class);

                System.out.println("successfully created new version with id '" + data.get("id") + "'");
                return true;
            } else {
                System.err.println("error raw: " + response.body());

                Map<String, Object> data = AutoPublisher.GSON.fromJson(response.body(), Map.class);
                System.err.println("error: " + data.get("error"));
                System.err.println("description: " + data.get("description"));
            }
        } catch (Exception e) {
            System.err.println("Error accessing API:");
            e.printStackTrace(System.err);
        }
        return false;
    }

    private static String[] fillModules(ArtifactInfo[] modules, boolean withSources) {
        List<String> strings = new ArrayList<>();
        strings.addFirst("primary");
        if (withSources) strings.add("primary-sources");
        for (ArtifactInfo module : modules) {
            strings.add(module.name());
            if (withSources)
                strings.add(module + "-sources");
        }
        return strings.toArray(String[]::new);
    }

    // Helper method to add a text field
    private static void addData(PrintWriter writer, String boundary, ModInfo modInfo, String mcVersion, Environment environment, String projectId, String[] modules, DependencyInfo[] dependencies) throws IOException {
        writer.append("""
                --%s\r
                Content-Disposition: form-data; name="data"\r
                Content-Type: application/json; charset=UTF-8\r
                \r
                """.formatted(boundary));
        writer.append(addVersionData(modInfo, mcVersion, projectId, dependencies, modules, environment)).append("\r\n");
        writer.flush();
    }

    // Helper method to add a file field
    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        writer.append("""
                --%s\r
                Content-Disposition: form-data; name="%s"; filename="%s"\r
                Content-Type: application/java-archive\r
                \r
                """.formatted(boundary, fieldName, file.getName()));
        writer.flush();

        Files.copy(file.toPath(), outputStream);
        outputStream.flush();
        writer.append("\r\n");
        writer.flush();
    }

    private static String addVersionData(ModInfo modInfo, String mcVersion, String projectId, DependencyInfo[] dependencies, String[] modules, Environment environment) throws IOException {
        Map<String, Object> data = new HashMap<>();

        data.put("name", String.format("%s v%s", modInfo.name(), modInfo.version()));
        data.put("version_number", modInfo.artifactVersion());
        data.put("loaders", new String[]{"neoforge"});
        data.put("game_versions", new String[]{mcVersion});
        data.put("version_type", "release");
        addDependencies(dependencies, data, mcVersion);
        data.put("featured", true);
        data.put("status", "listed");
        data.put("project_id", projectId);
        data.put("environment", environment.name().toLowerCase());

        data.put("file_parts", modules);
        data.put("primary_file", "primary");
        data.put("changelog", AutoPublisher.getChangelog());

        return AutoPublisher.GSON.toJson(data);
    }

    private static void addDependencies(DependencyInfo[] dependencies, Map<String, Object> data, String gameVersion) throws IOException {
        List<JsonObject> dependencyData = new ArrayList<>();

        if (dependencies != null) {
            for (DependencyInfo object : dependencies) {
                dependencyData.add(object.toModrinthDependency(gameVersion));
            }
        }

        data.put("dependencies", dependencyData);
    }

    public static String getDependencyVersionId(String modId, String gameVersion, String name, int ordinal) throws IOException {
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
