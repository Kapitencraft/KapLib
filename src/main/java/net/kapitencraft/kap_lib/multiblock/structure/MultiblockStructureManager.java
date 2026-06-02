package net.kapitencraft.kap_lib.multiblock.structure;

import com.google.common.collect.ImmutableMap;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.core.io.serialization.CodecJsonReloader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * manages multiblock structure loadable via datapack
 * features TODO
 * 1. manager interface to manage multiblock structures
 * 2. servant interface to manage multiblock IO
 * 3. add multiblock setup block similar to structure block to build multiblock structures
 * 4. make multiblock generate like {@link java.util.regex.Pattern}
 */
public class MultiblockStructureManager extends SimplePreparableReloadListener<MultiblockStructureManager.Data> {
    private @NotNull Map<ResourceLocation, MultiblockStructure> structures = Map.of();
    private @NotNull Map<ResourceLocation, MultiblockStructure.Placed> placed = Map.of();

    @Override
    protected @NotNull Data prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, MultiblockStructure> structureMap = new HashMap<>();
        Map<ResourceLocation, MultiblockStructure.Placed> placedMap = new HashMap<>();
        CodecJsonReloader.scanDirectory(resourceManager, "multiblock/structures", JsonHelper.GSON, MultiblockStructure.CODEC, structureMap);
        CodecJsonReloader.scanDirectory(resourceManager, "multiblock/placed", JsonHelper.GSON, MultiblockStructure.Placed.CODEC, placedMap);
        return new Data(ImmutableMap.copyOf(structureMap), ImmutableMap.copyOf(placedMap));
    }

    protected record Data(Map<ResourceLocation, MultiblockStructure> structures, Map<ResourceLocation, MultiblockStructure.Placed> placedStructures) {
    }

    @Override
    protected void apply(Data data, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.structures = data.structures();
        this.placed = data.placedStructures();
    }

    public @Nullable MultiblockStructure getStructure(ResourceLocation location) {
        return this.structures.get(location);
    }
}
