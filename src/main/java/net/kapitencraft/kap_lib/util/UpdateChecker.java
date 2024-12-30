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
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateChecker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, UpdateData> projectData = new HashMap<>();
    private static final String PROJECT_URL = "https://api.modrinth.com/v2/project/";
    private static final Config config = loadConfig();
    public static final List<Update> availableUpdates = new ArrayList<>();

    public static class Update {
        private final String modId;
        private final String version;
        private final String downloadURL;
        private final String newFileName, oldFileName;
        private final int size;

        public Update(String modId, String version, String downloadURL, String newFileName, String oldFileName, int size) {
            this.modId = modId;
            this.version = version;
            this.downloadURL = downloadURL;
            this.newFileName = newFileName;
            this.oldFileName = oldFileName;
            this.size = size;
        }

        public void download() {
            downloadAndSaveUpdate(downloadURL, newFileName, size);
        }
    }

    private static Config loadConfig() {
        File file = new File(KapLibMod.MAIN, "update_checker_config.json");
        return IOHelper.loadOrCreateFile(file, Config.CODEC, () -> new Config(false, ReleaseState.RELEASE));
    }

    public static void run() {
        RegisterUpdateCheckersEvent event = new RegisterUpdateCheckersEvent(UpdateChecker::registerUpdater);
        ModLoader.get().postEvent(event);
        Thread thread = new Thread(UpdateChecker::checkUpdates, "Update Checker");
        if (config.autoUpdate) thread.setPriority(10);
        thread.start();
    }

    private record Config(boolean autoUpdate, ReleaseState state) {
        private static final Codec<Config> CODEC = RecordCodecBuilder.create(configInstance -> configInstance
                .group(
                        Codec.BOOL.fieldOf("auto_update").forGetter(Config::autoUpdate),
                        ReleaseState.CODEC.fieldOf("release_state").forGetter(Config::state)
                ).apply(configInstance, Config::new)
        );
    }

    private enum ReleaseState implements StringRepresentable {
        RELEASE("release"),
        BETA("beta"),
        ALPHA("alpha");

        private final String name;

        ReleaseState(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        private boolean is(String val) {
            return this == ALPHA || (this == BETA ? !"alpha".equals(val) : "release".equals(val));
        }

        private static final EnumCodec<ReleaseState> CODEC = StringRepresentable.fromEnum(ReleaseState::values);
    }

    private static void registerUpdater(String projectId, String modId, Pattern versionExtractor) {
        projectData.put(projectId, new UpdateData(modId, versionExtractor));
    }

    private record UpdateData(String modId, Pattern versionExtractor) {
    }

    private static void checkUpdates() {
        info("Starting Update check... (auto update " + (config.autoUpdate ? "enabled" : "disabled") + ")");
        projectData.keySet().stream().map(UpdateChecker::checkUpdate);
        if (availableUpdates.isEmpty()) {
            info("all mods up to date");
            return;
        }
        info("Update check completed: " + availableUpdates.size() + " updates available");
        if (config.autoUpdate) {
            for (Update update : availableUpdates) {
                update.download();
            }
            System.exit(0); //drop system for reload
        }
    }

    private static Result checkUpdate(String projectId) {
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
                return Result.connectionFailed(updateData.modId, currentModVersion);
            } else {
                dataStream = connection.getInputStream();
            }
            JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

            JsonArray data = (JsonArray) Streams.parse(reader);

            reader.close();

            Stream<JsonObject> versionData = JsonHelper.castToObjects(data);
            Map<String, JsonObject> newer = versionData.collect(
                    CollectorHelper.toKeyMappedStream(
                            object -> {
                                Matcher matcher = updateData.versionExtractor.matcher(GsonHelper.getAsString(object, "version_number"));
                                if (matcher.matches()) {
                                    return matcher.group(1);
                                }
                                return null;
                            }
                    )
            )
                    .filterKeys(Objects::nonNull)
                    .mapKeys(ComparableVersion::new)
                    .filterValues(jsonObject -> config.state.is(GsonHelper.getAsString(jsonObject, "version_type")), null)
                    .filterKeys(comparableVersion -> currentModVersion.compareTo(comparableVersion) < 0)
                    .mapKeys(ComparableVersion::toString)
                    .toMap();
            if (newer.isEmpty()) {
                String msg = "Mod '" + updateData.modId + "' is up-to-date";
                info(msg);

                return Result.upToDate(updateData.modId, currentModVersion); //there's no newer versions, so we can skip the rest
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

            JsonObject newestVersionData = newer.get(newest.toString());
            JsonObject primaryFile = getPrimaryFile(newestVersionData);
            String fileUrl = GsonHelper.getAsString(primaryFile, "url");
            String fileName = GsonHelper.getAsString(primaryFile, "filename");
            int size = GsonHelper.getAsInt(primaryFile, "size");
            availableUpdates.add(new Update(updateData.modId, newest.toString(), fileUrl, fileName, modInfo.getFile().getFileName(), size));
            return Result.success(currentModVersion, newest, updateData.modId);
        } catch (IOException e) {
            LOGGER.warn("error checking for update on project '{}'", updateData.modId);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("provided pattern for mod '{}' was unable to parse version string '{}'", updateData.modId, e.getMessage());
        }
        return Result.failed(updateData.modId);
    }

    private record Result(@NotNull String modId, ComparableVersion currentVersion, ComparableVersion targetVersion, Type type) {

        public static Result success(ComparableVersion currentModVersion, ComparableVersion newest, String modId) {
            return new Result(modId, currentModVersion, newest, Type.UP_TO_DATE);
        }

        private enum Type {
            CONNECTION_FAILED,
            FAILED,
            UP_TO_DATE,
            OUTDATED
        }

        public static Result connectionFailed(String modId, ComparableVersion currentModVersion) {
            return new Result(modId, currentModVersion, null, Type.CONNECTION_FAILED);
        }

        public static Result upToDate(String modId, ComparableVersion currentModVersion) {
            return new Result(modId, currentModVersion, null, Type.UP_TO_DATE);
        }

        public static Result failed(String modId) {
            return new Result(modId, null, null, Type.FAILED);
        }

        public void log() {
            info(modId + " (version = " + (currentVersion == null ? "null" : currentVersion.getCanonical()) + ") found status " + type.name() + ", target = " + (targetVersion == null ? "null" : targetVersion.getCanonical()));
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static JsonObject getPrimaryFile(JsonObject newestVersionData) {
        return JsonHelper.castToObjects(GsonHelper.getAsJsonArray(newestVersionData, "files")).filter(o -> GsonHelper.getAsBoolean(o, "primary")).findAny().get();
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

                // output file
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
            }
        } catch (IOException e) {
            LOGGER.warn("error attempting to save file: {}", e.getMessage());
        }
    }

    private static void info(String msg) {
        StartupMessageManager.addModMessage(msg);
        LOGGER.info(Markers.UPDATE_CHECKER, msg);
    }

    private static String dependenciesToString(List<? extends IModInfo.ModVersion> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
    }
}
