package net.kapitencraft.kap_lib.multiblock.structure;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructureMatch;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructurePattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MultiblockStructureManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("MultiblockStructureManager");

    private Multimap<BlockState, StructurePositionEntry> entries;
    private MultiblockStructurePatternManager patternManager;

    public static MultiblockStructureManager INSTANCE = new MultiblockStructureManager();

    public void rebuild(MultiblockStructurePatternManager patternManager) {
        ImmutableMultimap.Builder<BlockState, StructurePositionEntry> builder = new ImmutableMultimap.Builder<>();
        Map<ResourceLocation, MultiblockStructurePattern> all = patternManager.getAll();
        all.forEach((resourceLocation, multiblockStructurePattern) -> {
            Multimap<BlockState, Vec3i> positions = multiblockStructurePattern.buildInfo();
            positions.forEach((blockState, vec3i) -> {
                builder.put(blockState, new StructurePositionEntry(resourceLocation, vec3i));
            });
        });
        this.entries = builder.build();
        this.patternManager = patternManager;
    }

    public void notifyChange(LevelAccessor level, BlockPos pos, BlockState newState) {
        if (entries.containsKey(newState)) {
            Collection<StructurePositionEntry> structurePositions = entries.get(newState);
            for (StructurePositionEntry structurePosition : structurePositions) {
                MultiblockStructurePattern pattern = this.patternManager.getStructure(structurePosition.structureLocation);
                List<BlockPos> origins = pattern.findPotentialOrigins(level, pos, structurePosition.posInStructure);
                for (BlockPos origin : origins) {
                    MultiblockStructureMatch match = pattern.match(level, origin);
                    if (match != null) {
                        LOGGER.debug("found new multiblock structure of type {} at origin {}, size {}",
                                structurePosition.structureLocation, TextHelper.positionToText(origin),
                                TextHelper.positionToText(match.getSize())
                        );
                    }
                }
            }
        }
    }

    private record StructurePositionEntry(ResourceLocation structureLocation, Vec3i posInStructure) {
    }
}
