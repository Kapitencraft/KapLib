package net.kapitencraft.kap_lib.publish;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.publish.changelog.Changelog;
import net.kapitencraft.kap_lib.publish.depends.DependencyInfo;
import net.kapitencraft.kap_lib.publish.depends.DependencyType;
import net.kapitencraft.kap_lib.publish.info.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.*;

public class AutoPublisher {
    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Config.class, new Config.Deserializer())
            .registerTypeAdapter(ModInfo.class, new ModInfo.Deserializer())
            .registerTypeAdapter(AuthorInfo.class, new AuthorInfo.Deserializer())
            .registerTypeAdapter(ChangelogInfo.class, new ChangelogInfo.Deserializer())
            .registerTypeAdapter(DependencyInfo.class, new DependencyInfo.Deserializer())
            .registerTypeAdapter(DependencyType.class, new DependencyType.Deserializer())
            .registerTypeAdapter(AssetsInfo.class, new AssetsInfo.Deserializer())
            .registerTypeAdapter(ArtifactInfo.class, new ArtifactInfo.Deserializer())
            .create();
    static final Logger LOGGER = LogUtils.getLogger();

    private static final File CONFIG = new File("build/resources/main/publish_config.json");
    private static final File DATA_CACHE = new File("run/PublishCache.txt");

    private static Config loadConfig() throws FileNotFoundException {
        FileReader reader = new FileReader(CONFIG);
        return GSON.fromJson(reader, Config.class);
    }

    public static void main(String[] args) {
        Config config;
        try {
            config = loadConfig();
        } catch (FileNotFoundException e) {
            LOGGER.error("Config not found.");
            return;
        }
        String modVersion = config.modInfo().version();
        if (DATA_CACHE.exists()) {
            try {
                String lastVersion = new String(Files.readAllBytes(DATA_CACHE.toPath()));
                if (lastVersion.equals(modVersion)) {
                    LOGGER.error("last published version matches current version"); //do not upload the same version twice
                    return;
                }
            } catch (IOException e) {
                LOGGER.warn("unable to read version cache: {}, ignoring", e.getMessage());
            }
        }

        LOGGER.debug("Verifying file existence...");
        List<Source> source = verifyFileExistence(config.modInfo().id(), config.modInfo().artifactVersion(), config.assetsInfo().sourcePath(), config.archives(), config.withSources());
        LOGGER.debug("successfully verified {} files", source.size());

        LOGGER.debug("Compiling Changelog...");
        try {
            createChangelog(config.changelogInfo() == null ? ChangelogInfo.DEFAULT : config.changelogInfo(), config.assetsInfo().changelogPath(), config.assetsInfo().categoriesPath());
        } catch (FileNotFoundException e) {
            LOGGER.error("changelog not found: {}", e.getMessage());
            return;
        }

        String mcVersion = config.mcVersion();
        String fmlVersion = config.loaderVersion();
        LOGGER.info("Auto Publish activated with args:");
        LOGGER.info("modId=\"{}\", modName=\"{}\", modVersion={}, mcVersion={}, loaderVersion={}", config.modInfo().id(), config.modInfo().name(), modVersion, mcVersion, fmlVersion);

        try {
            try (HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()) {
                if (
                    //  (config.curseforgeId == null || CurseforgePublish.publish(config, client, source)) &&
                        (config.modrinthId() == null || ModrinthPublish.publish(config, client, source))) {
                    clearChangelog(config.assetsInfo().changelogPath());
                    saveDataCache(modVersion);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error accessing API:");
            e.printStackTrace(System.err);
        }
    }

    //region file verify
    record Source(String moduleName, File obj) {
    }

    /**
     * returns a list of all files, including main, module and source if enabled
     */
    private static List<Source> verifyFileExistence(String modId, String artifactVersion, String sourcePath, ArtifactInfo[] archives, boolean withSources) {
        List<Source> sources = new ArrayList<>();
        String fileBase = modId + "-" + artifactVersion;
        sources.add(new Source("primary", checkFileExistence(sourcePath, fileBase)));
        if (withSources)
            sources.add(new Source("primary-sources", checkFileExistence(sourcePath, fileBase + "-sources")));
        for (ArtifactInfo archive : archives) {
            sources.add(new Source(archive.name(), checkFileExistence(archive.sourcePath(), archive.name())));
            if (withSources)
                sources.add(new Source(archive + "-sources", checkFileExistence(sourcePath, String.format("%s-sources", archive))));
        }

        return sources;
    }

    private static File checkFileExistence(String sourcePath, String fileBase) {
        File file = new File(sourcePath + "/" + fileBase + ".jar");
        if (!file.exists()) throw new NullPointerException("missing jar at " + file.getPath());
        return file;
    }
    //endregion

    //region changelog
    private static String changelog;

    static void createChangelog(ChangelogInfo info, String changelogLocation, String categoriesLocation) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(changelogLocation));
        Changelog log = info.createLog(categoriesLocation);
        log.parse(reader);
        changelog = info.compileLog(log);
    }

    static String getChangelog() {
        return changelog;
    }

    //endregion

    private static void saveDataCache(String modVersion) throws IOException {
        Files.createFile(DATA_CACHE.toPath());
        FileWriter writer = new FileWriter(DATA_CACHE);
        writer.write(modVersion);
        writer.close();
    }

    private static void clearChangelog(String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.close();
    }

    static String getAuth(boolean modrinth) {
        return modrinth ? System.getProperty("modrinthAuth") : System.getProperty("curseforgeAuth");
    }

    static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] arr : arrays) length += arr.length;
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }
        return result;
    }
}
