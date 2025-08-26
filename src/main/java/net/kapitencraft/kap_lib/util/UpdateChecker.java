package net.kapitencraft.kap_lib.util;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.event.custom.RegisterUpdateCheckersEvent;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.network.ModrinthUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateChecker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, UpdateData> projectData = new HashMap<>();
    private static final Config config = loadConfig();

    public static class Update {
        private final String downloadURL;
        private final String newFileName, oldFileName;
        private final int size;

        public Update(String downloadURL, String newFileName, String oldFileName, int size) {
            this.downloadURL = downloadURL;
            this.newFileName = newFileName;
            this.oldFileName = oldFileName;
            this.size = size;
        }

        public void download() {
            downloadAndSaveUpdate(downloadURL, newFileName, oldFileName, size);
        }
    }

    private static Config loadConfig() {
        File file = new File(KapLibMod.ROOT, "update_checker_config.json");
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
                        Codec.BOOL.optionalFieldOf("auto_update", false).forGetter(Config::autoUpdate),
                        ReleaseState.CODEC.optionalFieldOf("release_state", ReleaseState.RELEASE).forGetter(Config::state)
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
        List<Result> results = projectData.keySet().stream().map(UpdateChecker::checkUpdate).toList();
        List<Update> updates = new ArrayList<>();
        for (Result result : results) {
            result.log();
            if (result.type == Result.Type.OUTDATED) updates.add(result.update);
        }
        info(String.format("Update check completed: %s update(s) available", updates.size()));
        if (config.autoUpdate && !updates.isEmpty()) {
            for (Update update : updates) {
                update.download();
            }
            //drop system for reload
            info("Ending Programm.");
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft.getInstance().stop();
            } else {
                System.exit(0);
            }
        }
    }

    private static Result checkUpdate(String projectId) {
        UpdateData updateData = projectData.get(projectId);
        IModFileInfo modInfo = ModList.get().getModFileById(updateData.modId);
        if (modInfo == null) {
            LOGGER.warn("no mod under id '{}' found", updateData.modId);
            return Result.unknown(updateData.modId);
        }
        try {
            info("running version check on '" + projectId + "'");
            ComparableVersion currentModVersion = new ComparableVersion(modInfo.versionString());
            Stream<JsonObject> rawVersionData = ModrinthUtils.readVersions(projectId, MCPVersion.getMCVersion(), "KapLibAutoUpdater");
            if (rawVersionData == null) {
                LOGGER.warn("connection to {} failed", updateData.modId);
                return Result.connectionFailed(updateData.modId, currentModVersion);
            }

            Map<ComparableVersion, JsonObject> versionData = rawVersionData.collect(
                    CollectorHelper.toKeyMappedStream(
                            object -> {
                                String versionNumber = GsonHelper.getAsString(object, "version_number");
                                Matcher matcher = updateData.versionExtractor.matcher(versionNumber);
                                if (matcher.matches()) {
                                    return matcher.group(1);
                                }
                                LOGGER.warn("unable to extract version from '{}' of mod {}", versionNumber, updateData.modId);
                                return null;
                            }
                    )
            )
                    .filterKeys(Objects::nonNull)
                    .mapKeys(ComparableVersion::new).toMap();

            List<ComparableVersion> versions = new ArrayList<>(versionData.keySet());
            versions.sort(ComparableVersion::compareTo);

            ComparableVersion newest = versions.get(versions.size() - 1);
            int compare = newest.compareTo(currentModVersion);
            if (compare > 0) {
                JsonObject newestVersionData = versionData.get(newest);
                JsonObject primaryFile = getPrimaryFile(newestVersionData);
                String fileUrl = GsonHelper.getAsString(primaryFile, "url");
                String fileName = GsonHelper.getAsString(primaryFile, "filename");
                int size = GsonHelper.getAsInt(primaryFile, "size");
                return Result.outdated(
                        currentModVersion,
                        newest,
                        new Update(fileUrl, fileName, modInfo.getFile().getFileName(), size),
                        updateData.modId);
            } else if (compare == 0)
                return Result.upToDate(updateData.modId, currentModVersion);
            return Result.ahead(updateData.modId, currentModVersion, newest);
        } catch (IOException e) {
            LOGGER.warn("error checking for update on project '{}'", updateData.modId);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            LOGGER.warn("provided pattern for mod '{}' was unable to parse version string '{}'", updateData.modId, e.getMessage());
        }
        return Result.failed(updateData.modId);
    }

    private record Result(@NotNull String modId, ComparableVersion currentVersion, ComparableVersion targetVersion, Update update, Type type) {

        private enum Type {
            CONNECTION_FAILED,
            FAILED,
            UP_TO_DATE,
            OUTDATED,
            UNKNOWN,
            AHEAD
        }


        public static Result unknown(String modId) {
            return new Result(modId, null, null, null, Type.UNKNOWN);
        }

        public static Result outdated(ComparableVersion currentModVersion, ComparableVersion newest, Update update, String modId) {
            return new Result(modId, currentModVersion, newest, update, Type.OUTDATED);
        }

        public static Result ahead(String modId, ComparableVersion currentModVersion, ComparableVersion newest) {
            return new Result(modId, currentModVersion, newest, null, Type.AHEAD);
        }

        public static Result connectionFailed(String modId, ComparableVersion currentModVersion) {
            return new Result(modId, currentModVersion, null, null, Type.CONNECTION_FAILED);
        }

        public static Result upToDate(String modId, ComparableVersion currentModVersion) {
            return new Result(modId, currentModVersion, null, null, Type.UP_TO_DATE);
        }

        public static Result failed(String modId) {
            return new Result(modId, null, null, null, Type.FAILED);
        }

        public void log() {
            info(String.format("status for '%s': %s, current=%s, target=%s", modId, type, currentVersion, targetVersion));
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static JsonObject getPrimaryFile(JsonObject newestVersionData) {
        return JsonHelper.castToObjects(GsonHelper.getAsJsonArray(newestVersionData, "files")).filter(o -> GsonHelper.getAsBoolean(o, "primary")).findAny().get();
    }

    private static void downloadAndSaveUpdate(String fileUrl, String fileName, String oldFileName, int size) {
        try {
            info("Updating '" + fileName + "'");
            File modsDir = new File("./mods");

            File oldFile = new File(modsDir, oldFileName);
            if (!oldFile.exists()) throw new IOException("Old file '" + oldFileName + "' not found");

            URL url = new URL(fileUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            // Check for successful response code
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get input stream and content disposition (file name)
                InputStream inputStream = httpConnection.getInputStream();

                // output file
                File outputTarget = new File(modsDir, fileName);

                // Open output stream to save file
                FileOutputStream outputStream = new FileOutputStream(outputTarget);

                byte[] buffer = new byte[4096]; //read 4kb at once
                ProgressMeter downloadProgress = StartupMessageManager.addProgressBar("Downloading '" + fileName + "'", size);
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    downloadProgress.setAbsolute(downloadProgress.current() + bytesRead);
                }

                outputStream.close();
                inputStream.close();
                downloadProgress.complete();
                if (!oldFile.delete()) {
                    throw new IOException("unable to delete old file '" + oldFileName + "'");
                };
            }
        } catch (IOException e) {
            LOGGER.warn("error attempting to save update file: {}", e.getMessage());
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
