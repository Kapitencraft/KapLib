package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * currently not used because curseforge is weird
 */
public class CurseforgePublish {
    private static final String API_URL = "https://minecraft.curseforge.com/api/projects/";

    //TODO make working
    static boolean publish(AutoPublisher.Config config) {
        String modId = config.modId();
        String modName = config.modName();
        String modVersion = config.modVersion();
        String mcVersion = config.mcVersion();
        String fmlVersion = config.fmlVersion();
        try {
            URL url = new URL(API_URL + modId + "/upload-file");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            String boundary = "----Boundary" + UUID.randomUUID();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", String.format(config.author() + "/%s/%s (%s)", modName, modVersion, config.email()));
            connection.setRequestProperty("Authorization", AutoPublisher.getAuth(true));

            String fileBase = String.format("./build/libs/%s-", modId) + AutoPublisher.formatVersion(modVersion, mcVersion, fmlVersion);

            File mainFile = new File(fileBase + ".jar");


            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

                // Add text part
                addData(writer, boundary, modName, modVersion, mcVersion, fmlVersion, config.projectId(), config.extraFiles(), config.dependencies());

                // Add file part
                //addFilePart(writer, outputStream, boundary, "primary", mainFile);

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
            } else {
                dataStream = connection.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(dataStream));

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

    private static void addData(PrintWriter writer, String boundary, String modName, String modVersion, String mcVersion, String forgeVersion, String projectId, String[] extraFiles, JsonObject[] dependencies) {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"data\"\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        //writer.append(addVersionData(modName, modVersion, mcVersion, forgeVersion, projectId, dependencies, extraFiles)).append("\r\n");
        writer.flush();
    }

    private static String addVersionData(String modName, String modVersion, String mcVersion, String forgeVersion, String projectId, JsonObject[] dependencies, String[] extraFiles) throws IOException {
        Map<String, Object> data = new HashMap<>();

        data.put("changelog", AutoPublisher.createChangelog());
        data.put("changelogType", "html");
        data.put("displayName", String.format("%s v%s", modName, modVersion));
        //data.put("version_number", AutoPublisher.formatVersion(modVersion, mcVersion, forgeVersion));
        //data.put("loaders", new String[] {"forge"});
        data.put("game_versions", new String[]{mcVersion});
        data.put("releaseType", "release");
        //addDependencies(dependencies, data);
        data.put("featured", true);
        data.put("status", "listed");
        data.put("project_id", projectId);
        String[] fileParts = new String[extraFiles.length + 1];
        System.arraycopy(extraFiles, 0, fileParts, 1, extraFiles.length);
        fileParts[0] = "primary";
        data.put("file_parts", fileParts);
        data.put("primary_file", "primary");

        return AutoPublisher.GSON.toJson(data);
    }
}
