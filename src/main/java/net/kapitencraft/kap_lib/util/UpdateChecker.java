package net.kapitencraft.kap_lib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.event.custom.RegisterUpdateCheckersEvent;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class UpdateChecker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, UpdateData> projectData = new HashMap<>();
    private static final String PROJECT_URL = "https://api.modrinth.com/v2/project/";
    private static final Config config = loadConfig();
    private static boolean updateExecuted = false;

    private static Config loadConfig() {
        File file = new File(KapLibMod.MAIN, "update_checker_config.json");
        return IOHelper.loadOrCreateFile(file, Config.CODEC, () -> new Config(false));
    }

    public static void run() {
        RegisterUpdateCheckersEvent event = new RegisterUpdateCheckersEvent(UpdateChecker::registerUpdater);
        ModLoader.get().postEvent(event);
        Thread thread = new Thread(UpdateChecker::checkUpdates, "Update Checker");
        thread.start();
    }

    private record Config(boolean autoUpdate) {
        private static final Codec<Config> CODEC = RecordCodecBuilder.create(configInstance -> configInstance
                .group(
                        Codec.BOOL.fieldOf("auto_update").forGetter(Config::autoUpdate)
                ).apply(configInstance, Config::new)
        );
    }

    private static void registerUpdater(String projectId, String modId) {
        projectData.put(projectId, new UpdateData(modId));
    }

    private record UpdateData(String modId) {
    }

    private static void checkUpdates() {
        StartupMessageManager.addModMessage("Starting Update check...");
        LOGGER.info(Markers.UPDATE_CHECKER, "Starting Update check...");
        projectData.keySet().forEach(UpdateChecker::checkUpdate);
        if (config.autoUpdate && updateExecuted) System.exit(0);
    }

    private static void checkUpdate(String projectId) {
        UpdateData updateData = projectData.get(projectId);
        IModFileInfo modInfo = ModList.get().getModFileById(updateData.modId);
        try {
            info("running version check on '" + projectId + "'");
            ComparableVersion currentModVersion = new ComparableVersion(modInfo.versionString());
            String projectVersionURL = PROJECT_URL + projectId + "/version";
            String requestParams = "?loaders=" +
                    URLEncoder.encode(JsonHelper.GSON.toJson(new String[] {"forge"}), StandardCharsets.UTF_8) +
                    "&game_versions=" + URLEncoder.encode(JsonHelper.GSON.toJson(new Object[] {MCPVersion.getMCVersion()}), StandardCharsets.UTF_8);
            URL url = new URL(projectVersionURL + requestParams);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "KapLibAutoUpdater for '" + projectId + "', (kapitencraft@gmail.com)");

            int response = connection.getResponseCode();

            InputStream dataStream;
            if (response != HttpsURLConnection.HTTP_OK) {
                LOGGER.warn("connection to {} failed: {}", updateData.modId, response);
                dataStream = connection.getErrorStream();
            } else {
                dataStream = connection.getInputStream();
            }
            JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

            JsonArray data = (JsonArray) Streams.parse(reader);

            reader.close();

            Stream<JsonObject> versionData = JsonHelper.castToObjects(data);
            Map<String, JsonObject> newer = versionData.collect(
                    CollectorHelper.toKeyMappedStream(
                            object -> GsonHelper.getAsString(object, "version_number")
                    )
            )
                    .mapKeys(ComparableVersion::new)
                    .filterKeys(comparableVersion -> currentModVersion.compareTo(comparableVersion) < 0)
                    .mapKeys(ComparableVersion::toString)
                    .toMap();
            if (newer.isEmpty()) {
                String msg = "Mod '" + updateData.modId + "' is up-to-date";
                info(msg);

                return; //there's no newer versions, so we can skip the rest
            }
            ComparableVersion newest = null;
            for (String s : newer.keySet()) {
                ComparableVersion v = new ComparableVersion(s);
                if (newest == null || v.compareTo(newest) > 0) {
                    newest = v;
                }
            }
            String msg = String.format("Found newer version for mod '%s': %s", updateData.modId, newest);
            info(msg);

            if (config.autoUpdate) {
                JsonObject newestVersionData = newer.get(newest.toString());
                String fileUrl = GsonHelper.getAsString(newestVersionData, "url");
                String fileName = GsonHelper.getAsString(newestVersionData, "filename");
                int size = GsonHelper.getAsInt(newestVersionData, "size");
                downloadAndSaveUpdate(fileUrl, fileName, size);
            }
        } catch (IOException e) {
            LOGGER.warn("error checking for update on project '{}'", updateData.modId);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("provided pattern for mod '{}' does not have one group", updateData.modId);
        }
    }

    private static void downloadAndSaveUpdate(String fileUrl, String fileName, int size) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            // Check for successful response code
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get input stream and content disposition (file name)
                InputStream inputStream = httpConnection.getInputStream();

                // Save the file to the specified directory
                File outputTarget = new File("./mods/" + fileName);

                // Open output stream to save file
                FileOutputStream outputStream = new FileOutputStream(outputTarget);

                byte[] buffer = new byte[4096];
                ProgressMeter downloadProgress = StartupMessageManager.addProgressBar("Downloading '" + fileName + "'", size);
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    downloadProgress.setAbsolute(downloadProgress.current() + bytesRead);
                }

                outputStream.close();
                inputStream.close();
                downloadProgress.complete();
                updateExecuted = true;
            }
        } catch (IOException e) {
            LOGGER.warn("error attempting to save file: {}", e.getMessage());
        }
    }

    private static void info(String msg) {
        StartupMessageManager.addModMessage(msg);
        LOGGER.info(msg);
    }
}
