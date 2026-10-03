package net.kapitencraft.kap_lib.multiblock.structure;

import com.google.common.collect.ImmutableMap;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.core.io.serialization.CodecJsonReloader;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructurePattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * manages multiblock structure configurations loadable via datapack
 * features TODO
 * 1. manager interface to manage multiblock structures
 * 2. servant interface to manage multiblock IO
 * 3. add multiblock setup block similar to structure block to build multiblock structures
 * 4. make multiblock generate like {@link java.util.regex.Pattern}
 */
public class MultiblockStructurePatternManager extends CodecJsonReloader<MultiblockStructureConfiguration> {
    //do we need to sync this?
    public static final MultiblockStructurePatternManager INSTANCE = new MultiblockStructurePatternManager();
    private @NotNull Map<ResourceLocation, MultiblockStructurePattern> structures = Map.of();

    private MultiblockStructurePatternManager() {
        super(MultiblockStructureConfiguration.CODEC, JsonHelper.GSON, "mb_configs");
    }

    @Override
    protected void apply(Map<ResourceLocation, MultiblockStructureConfiguration> structureConfigurations, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, MultiblockStructurePattern> patternBuilder = ImmutableMap.builder();
        structureConfigurations.forEach((key, structureConfiguration) -> {
            patternBuilder.put(key, MultiblockStructurePattern.build(structureConfiguration));
        });
        this.structures = patternBuilder.build();
    }

    public @Nullable MultiblockStructurePattern getStructure(ResourceLocation location) {
        return this.structures.get(location);
    }
}
