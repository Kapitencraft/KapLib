package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * <b>READ AT YOUR OWN RISK</b><br>
 * Curseforge's API is atrocious and should not be used. Yet I, the craziest of all the modders,<br> have still taken a deep dive into it's disgusting data structure and have found a way to make it work (I sincerely hope)
 */
public class CurseforgePublish {

    private static final String API_URL = "https://minecraft.curseforge.com/api/projects/";
    private static final String VERSION_API_URL = "https://minecraft.curseforge.com/api/game/versions";

    /**
     * @param config the config to use for the publishing
     * @return whether the publishing was successful or not
     */
    //TODO make working
    static boolean publish(AutoPublisher.Config config, HttpClient client) {
        String modId = config.modInfo().id();
        String modName = config.modInfo().name();
        String modVersion = config.modInfo().version();
        String mcVersion = config.mcVersion();
        String loaderVersion = config.loaderVersion();
        Integer mcVersionId;
        Integer loaderVersionId;
        Integer loaderId;
        try {
            {
                //one element: {"id":14271,"gameVersionTypeID":3,"name":"60.0.20","slug":"60-0-20","apiVersion":null}
                HttpRequest.Builder request = HttpRequest.newBuilder().uri(URI.create(VERSION_API_URL))
                        .GET();
                appendAuth(request, config, modName, modVersion);

                HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());

                int versionResponse = response.statusCode();
                if (versionResponse != HttpsURLConnection.HTTP_OK) {
                    AutoPublisher.LOGGER.error("failed: {}", versionResponse);
                    AutoPublisher.LOGGER.warn(response.body());
                    return false;
                } else {
                    List<Map<String, Object>> list = AutoPublisher.GSON.fromJson(response.body(), List.class);
                    Map<String, Integer> versionLookup = new HashMap<>();
                    for (Map<String, Object> map : list) {
                        versionLookup.put(((String) map.get("name")), ((Double) map.get("id")).intValue());
                    }
                    mcVersionId = versionLookup.get(mcVersion);
                    loaderVersionId = versionLookup.get(loaderVersion);
                    loaderId = versionLookup.get("NeoForge");
                }
                if (mcVersionId == null) {
                    AutoPublisher.LOGGER.error("unknown version id for version {}", mcVersion);
                    return false;
                }
                if (loaderVersionId == null) {
                    AutoPublisher.LOGGER.error("unknown loader version id for loader version {}", loaderVersion);
                    return false;
                }
                if (loaderId == null) {
                    AutoPublisher.LOGGER.error("could not find get version id for NeoForge (uh oh)");
                    return false;
                }
            }
            int[] versions = new int[] {mcVersionId, loaderVersionId, loaderId};

            String fileBase = String.format("./build/libs/%s-", modId) + AutoPublisher.formatVersion(modVersion, mcVersion);

            File mainFile = new File(fileBase + ".jar");

            String boundary = "----Boundary" + UUID.randomUUID();

            // Add text part
            String requestData = getData(boundary, modName, modVersion, config.modules(), config.dependencies(), versions);

            // Add file part
            String fileHeader = getFileHeader(boundary, mainFile);

            byte[] fileData = Files.readAllBytes(mainFile.toPath());

            String fileFooter = "\r\n--" + boundary + "--\r\n";

            byte[] requestBody = AutoPublisher.concat(
                    requestData.getBytes(),
                    fileHeader.getBytes(),
                    fileData,
                    fileFooter.getBytes()
            );

            //TODO
            for (String extraFile : config.modules()) {
                File sourcesFile = new File(fileBase + String.format("-%s.jar", extraFile));
                //addFilePart(writer, outputStream, boundary, extraFile, sourcesFile);
            }

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + config.curseforgeId() + "/upload-file"));
            appendAuth(builder, config, modName, modVersion);
            builder.header("Accept", "application/json");
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)).build();

            AutoPublisher.LOGGER.info("publishing curseforge...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //response
            int responseCode = response.statusCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                AutoPublisher.LOGGER.error("failed: {}", responseCode);
                AutoPublisher.LOGGER.warn(response.body());
                return false;
            }

            Map<String, Object> data = AutoPublisher.GSON.fromJson(response.body(), Map.class);

            AutoPublisher.LOGGER.info("successfully created new version with id '{}'", data.get("id"));
            return true;
        } catch (Exception e) {
            AutoPublisher.LOGGER.error("Error accessing API:");
            e.printStackTrace(System.err);
        }
        return false;
    }

    private static void appendAuth(HttpRequest.Builder request, AutoPublisher.Config config, String modName, String modVersion) {
        request.header("User-Agent", String.format(config.authorInfo().name() + "/%s/%s (%s)", modName, modVersion, config.authorInfo().email()));
        request.header("X-Api-Token", AutoPublisher.getAuth(false));
    }

    private static String getData(String boundary, String modName, String modVersion, String[] extraFiles, AutoPublisher.DependencyInfo[] dependencies, int[] versionData) throws IOException {
        return """
                --%s\r
                Content-Disposition: form-data; name="metadata"\r
                Content-Type: application/json\r
                \r
                %s\r
                """.formatted(boundary, getVersionData(modName, modVersion, dependencies, extraFiles, versionData));
    }

    private static String getVersionData(String modName, String modVersion, AutoPublisher.DependencyInfo[] dependencies, String[] extraFiles, int[] versionData) throws IOException {
        Map<String, Object> data = new HashMap<>();

        data.put("changelog", AutoPublisher.getChangelog());
        data.put("changelogStyle", "html");
        data.put("displayName", String.format("%s v%s", modName, modVersion));
        data.put("gameVersions", versionData);
        data.put("releaseType", "release");
        addDependencies(dependencies, data);
        String[] fileParts = new String[extraFiles.length + 1];
        System.arraycopy(extraFiles, 0, fileParts, 1, extraFiles.length);
        fileParts[0] = "primary";
        data.put("file_parts", fileParts);
        data.put("primary_file", "primary");

        return AutoPublisher.GSON.toJson(data);
    }

    // Helper method to add a file field //TODO figure out how to upload modules
    private static String getFileHeader(String boundary, File file) {
        return """
                --%s\r
                Content-Disposition: form-data; name="file"; filename="%s"\r
                Content-Type:application/java-archive\r
                \r
                """.formatted(boundary, file.getName());
    }

    private static void addDependencies(AutoPublisher.DependencyInfo[] dependencies, Map<String, Object> data) throws IOException {
        if (dependencies == null || dependencies.length < 1) return; //no need to add all this information if there's no dependency to add
        Map<String, Object> map = new HashMap<>();
        List<JsonObject> dependencyData = new ArrayList<>();
        map.put("projects", dependencyData);

        for (AutoPublisher.DependencyInfo info : dependencies) {
            dependencyData.add(info.toCurseforgeDependency());
        }

        data.put("relations", map);
    }
}
