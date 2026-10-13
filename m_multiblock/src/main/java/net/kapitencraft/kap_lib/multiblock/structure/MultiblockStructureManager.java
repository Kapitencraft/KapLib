package net.kapitencraft.kap_lib.multiblock.structure;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructurePattern;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class MultiblockStructureManager {
    private Multimap<BlockState, StructurePositionEntry> entries;

    public static MultiblockStructureManager INSTANCE = new MultiblockStructureManager();

    public void rebuild(MultiblockStructurePatternManager patternManager) {
        ImmutableMultimap.Builder<BlockState, StructurePositionEntry> builder = new ImmutableMultimap.Builder<>();
        Map<ResourceLocation, MultiblockStructurePattern> all = patternManager.getAll();
        all.forEach((resourceLocation, multiblockStructurePattern) -> {
        });
    }

    private record StructurePositionEntry(ResourceLocation structureLocation, Vec3i posInStructure) {}
}
