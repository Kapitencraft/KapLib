package net.kapitencraft.kap_lib.core.io.serialization;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * allows to easily scan all files in the given directory and attempt to load each with the given codec,
 * <br>exposing the results for the developer to use
 * @param <T> the type of the result
 */
public abstract class CodecJsonReloader<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Codec<T> entryCodec;
    private final Gson gson;
    private final String directory;

    /**
     * creates a new CJR
     * @param entryCodec the result entry codec
     * @param gson the gson to parse the files into JSON
     * @param directory the directory to target
     */
    public CodecJsonReloader(Codec<T> entryCodec, Gson gson, String directory) {
        this.entryCodec = entryCodec;
        this.gson = gson;
        this.directory = directory;
    }

    /**
     * scans the given file for json files and attempts to convert them into instances of the given codecs type
     * @param resourceManager the ResourceManager
     * @param name the name of the directory to search
     * @param gson the GSON to convert the resource into JSON
     * @param entryCodec the codec to convert the JSON into objects
     * @param output the aggregator to obtain the parsed entries
     * @param <T> the type of the resulting objects
     */
    public static <T> void scanDirectory(ResourceManager resourceManager, String name, Gson gson, Codec<T> entryCodec, Map<ResourceLocation, T> output) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(name);

        for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation fileLocation = entry.getKey();
            ResourceLocation idLocation = filetoidconverter.fileToId(fileLocation);

            try (Reader reader = entry.getValue().openAsReader()) {
                T element = entryCodec.decode(JsonOps.INSTANCE, GsonHelper.fromJson(gson, reader, JsonElement.class)).getOrThrow().getFirst();
                T oldElement = output.put(idLocation, element);
                if (oldElement != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + idLocation);
                }
            } catch (IOException | JsonParseException | IllegalArgumentException jsonparseexception) {
                LOGGER.error("Couldn't parse data file {} from {}", idLocation, fileLocation, jsonparseexception);
            }
        }
    }

    protected final @NotNull Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, T> map = new HashMap<>();
        scanDirectory(resourceManager, this.directory, this.gson, this.entryCodec, map);
        return ImmutableMap.copyOf(map);
    }
}
