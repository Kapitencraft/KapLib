package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MultiblockStructurePattern {
    private final Map<Direction.Axis, BlockPattern> sizeAgents;

    private MultiblockStructurePattern(Map<Direction.Axis, BlockPattern> sizeAgents) {
        this.sizeAgents = sizeAgents;
    }

    /**
     * builds a pattern from the structure.
     */
    public static MultiblockStructurePattern build(MultiblockStructureConfiguration structure) {
        Map<Direction.Axis, List<Quantifier>> quantifiers = structure.getQuantifiers();

        Vec3i size = structure.getSize();

        Map<Direction.Axis, BlockPattern> sizeGatherer = new EnumMap<>(Direction.Axis.class);
        for (Direction.Axis value : Direction.Axis.values()) {
            List<Quantifier> list = quantifiers.get(value);
            int sizeDim = size.get(value);
            sizeGatherer.put(value, BlockPattern.build(list, i -> {
                BlockPos pos = BlockPos.ZERO.relative(value, i);
                return structure.getInstanceAt(pos.getX(), pos.getY(), pos.getZ());
            }, sizeDim));
        }
        return new MultiblockStructurePattern(sizeGatherer);
    }

    public MultiblockStructureMatch match(Level level, BlockPos origin) {
        for (Direction.Axis value : Direction.Axis.values()) {
            BlockPattern node = this.sizeAgents.get(value);
            node.matcher(level);
        }

        return null;
    }
}
