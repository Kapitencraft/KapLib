package net.kapitencraft.kap_lib.spawn_table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SpawnTableProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    public static TriFunction<ResourceLocation, JsonElement, ResourceManager, Optional<SpawnTable>> getSpawnTableSerializer(Gson gson, String directory)
    {
        return (location, data, resourceManager) -> {
            try {
                Resource resource = resourceManager.getResource(location.withPath(directory + "/" + location.getPath() + ".json")).orElse(null);
                boolean custom = resource == null || !resource.isBuiltin();
                return Optional.of(loadLootTable(gson, location, data, custom));
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse element {}:{}", directory, location, exception);
                return Optional.empty();
            }
        };
    }

    public static SpawnTable loadLootTable(Gson gson, ResourceLocation name, JsonElement data, boolean custom) {
        SpawnTable ret = gson.fromJson(data, SpawnTable.class);
        ret.setId(name);

        ret.freeze();

        return ret;
    }
}
