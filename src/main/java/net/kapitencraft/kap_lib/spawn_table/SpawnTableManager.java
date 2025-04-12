package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.Optional;

public class SpawnTableManager extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, SpawnTable> spawnTables;

    public static SpawnTableManager instance;

    public SpawnTableManager() {
        super(JsonHelper.GSON, "spawn_tables");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, SpawnTable> map = new ImmutableMap.Builder<>();
        pObject.forEach((location, element) -> {
            Optional<SpawnTable> optional = SpawnTable.DATA_TYPE.deserialize(location, element, pResourceManager);
            optional.ifPresent(table -> map.put(location, table));
        });
        this.spawnTables = map.build();
    }

    public SpawnTable getSpawnTable(ResourceLocation name) {
        return spawnTables.get(name);
    }
}
