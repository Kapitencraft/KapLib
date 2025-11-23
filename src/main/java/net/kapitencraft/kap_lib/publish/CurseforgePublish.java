package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
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
    static boolean publish(AutoPublisher.Config config) {
        String modId = config.modId();
        String modName = config.modName();
        String modVersion = config.modVersion();
        String mcVersion = config.mcVersion();
        String loaderVersion = config.loaderVersion();
        Integer mcVersionId;
        Integer loaderVersionId;
        Integer loaderId;
        try {
            {
                //one element: {"id":14271,"gameVersionTypeID":3,"name":"60.0.20","slug":"60-0-20","apiVersion":null}
                URL version = new URL(VERSION_API_URL);
                HttpsURLConnection versionConnection = (HttpsURLConnection) version.openConnection();
                appendAuth(versionConnection, config, modName, modVersion);
                versionConnection.setRequestMethod("GET");
                versionConnection.setDoOutput(true);

                int versionResponse = versionConnection.getResponseCode();
                InputStream dataStream;
                if (versionResponse != HttpsURLConnection.HTTP_OK) {
                    System.err.println("failed: " + versionResponse);
                    dataStream = versionConnection.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));
                    reader.lines().forEach(System.out::println);
                    reader.close();
                    return false;
                } else {
                    dataStream = versionConnection.getInputStream();
                    JsonReader reader = new JsonReader(new InputStreamReader(dataStream));
                    List<Map<String, Object>> list = AutoPublisher.GSON.fromJson(reader, List.class);
                    Map<String, Integer> versionLookup = new HashMap<>();
                    for (Map<String, Object> map : list) {
                        versionLookup.put(((String) map.get("name")), ((Double) map.get("id")).intValue());
                    }
                    reader.close();
                    mcVersionId = versionLookup.get(mcVersion);
                    loaderVersionId = versionLookup.get(loaderVersion);
                    loaderId = versionLookup.get("NeoForge");
                }
                if (mcVersionId == null) {
                    System.err.println("unknown version id for version " + mcVersion);
                    return false;
                }
                if (loaderVersionId == null) {
                    System.err.println("unknown loader version id for loader version " + loaderVersion);
                    return false;
                }
                if (loaderId == null) {
                    System.err.println("could not find get version id for NeoForge (uh oh)");
                    return false;
                }
            }
            int[] versions = new int[] {mcVersionId, loaderVersionId, loaderId};
            
            URL url = new URL(API_URL + modId + "/upload-file");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            String boundary = "----Boundary" + UUID.randomUUID();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            appendAuth(connection, config, modName, modVersion);

            String fileBase = String.format("./build/libs/%s-", modId) + AutoPublisher.formatVersion(modVersion, mcVersion);

            File mainFile = new File(fileBase + ".jar");

            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

                // Add text part
                addData(writer, boundary, modName, modVersion, config.extraFiles(), config.dependencies(), versions);

                // Add file part
                addFilePart(writer, outputStream, boundary, mainFile);

                for (String extraFile : config.extraFiles()) {
                    File sourcesFile = new File(fileBase + String.format("-%s.jar", extraFile));
                    //addFilePart(writer, outputStream, boundary, extraFile, sourcesFile);
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));
                reader.lines().forEach(System.out::println);
                reader.close();
                return false;
            } else {
                dataStream = connection.getInputStream();
            }

            JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

            Map<String, Object> data = AutoPublisher.GSON.fromJson(reader, Map.class);

            reader.close();

            System.out.println("successfully created new version with id '" + data.get("id") + "'");
            return true;
        } catch (Exception e) {
            System.err.println("Error accessing API:");
            e.printStackTrace(System.err);
        }
        return false;
    }

    private static void appendAuth(HttpsURLConnection connection, AutoPublisher.Config config, String modName, String modVersion) {
        connection.setRequestProperty("User-Agent", String.format(config.author() + "/%s/%s (%s)", modName, modVersion, config.email()));
        connection.setRequestProperty("X-Api-Token", AutoPublisher.getAuth(false));

    }

    private static void addData(PrintWriter writer, String boundary, String modName, String modVersion, String[] extraFiles, JsonObject[] dependencies, int[] versionData) throws IOException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"metadata\"\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        writer.append(addVersionData(modName, modVersion, dependencies, extraFiles, versionData)).append("\r\n");
        writer.flush();
    }

    private static String addVersionData(String modName, String modVersion, JsonObject[] dependencies, String[] extraFiles, int[] versionData) throws IOException {
        Map<String, Object> data = new HashMap<>();

        data.put("changelog", AutoPublisher.createChangelog());
        data.put("changelogType", "html");
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
    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, File file) throws IOException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
        writer.append("Content-Type: application/java-archive\r\n\r\n");
        writer.flush();

        Files.copy(file.toPath(), outputStream);
        outputStream.flush();
        writer.append("\r\n");
        writer.flush();
    }

    private static final Map<String, String> DEPENDENCY_TYPE_CONVERTER = Map.of(
            "embedded", "embeddedLibrary",
            "incompatible", "incompatible",
            "optional", "optionalDependency",
            "required", "requiredDependency"
    );

    @SuppressWarnings("SuspiciousMethodCalls")
    private static void addDependencies(JsonObject[] dependencies, Map<String, Object> data) throws IOException {
        if (dependencies.length < 1) return; //no need to add all this information if there's no dependency to add
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> dependencyData = new ArrayList<>();
        map.put("projects", dependencyData);

        for (JsonObject object : dependencies) {

            Map<String, Object> dependency = AutoPublisher.GSON.fromJson(object, Map.class);
            if (!dependency.containsKey("project_id")) AutoPublisher.LOGGER.error("Dependency missing project id!");
            else if (!dependency.containsKey("version_name")) AutoPublisher.LOGGER.error("Dependency missing file name!");
            else if (!dependency.containsKey("dependency_type")) AutoPublisher.LOGGER.error("Dependency missing dependency type");
            else if (!AutoPublisher.verifyDependencyType(dependency.get("dependency_type"))) AutoPublisher.LOGGER.error("Unknown dependency type\nallowed: [required, optional, incompatible, embedded]");
            else {
                Map<String, Object> convertedDependency = new HashMap<>();
                convertedDependency.put("slug", dependency.get("project_id"));
                convertedDependency.put("type", DEPENDENCY_TYPE_CONVERTER.get(dependency.get("dependency_type")));
                dependencyData.add(convertedDependency);
                continue;
            }
            throw new IOException("Dependency Load Failed");
        }

        data.put("relations", map);
    }
}
