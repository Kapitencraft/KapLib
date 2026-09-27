package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.*;

public class MultiblockStructurePattern {
    private final Map<Direction.Axis, BlockPattern> sizeAgents;
    private final MultiblockStructureConfiguration structureConfiguration;

    private MultiblockStructurePattern(Map<Direction.Axis, BlockPattern> sizeAgents, MultiblockStructureConfiguration structure) {
        this.sizeAgents = sizeAgents;
        this.structureConfiguration = structure;
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
        return new MultiblockStructurePattern(sizeGatherer, structure);
    }

    public MultiblockStructureMatch match(Level level, BlockPos origin) {
        List<int[]>[] groupData = new List[3];
        Direction.Axis[] values = Direction.Axis.values();
        for (int i = 0; i < values.length; i++) {
            Direction.Axis value = values[i];
            BlockPattern node = this.sizeAgents.get(value);
            BlockMatcher matcher = node.matcher(level);
            if (!matcher.matches(
                    Direction.fromAxisAndDirection(value, Direction.AxisDirection.POSITIVE),
                    origin
            ))
                return null;
            List<Integer>[] quantifiers = matcher.quantifierList;
            List<int[]> dimensionalPermutations = new ArrayList<>();
            int[] permutation = new int[quantifiers.length];
            collectQuantifiers(quantifiers, 0, permutation, dimensionalPermutations);
            groupData[i] = dimensionalPermutations;
        }

        List<int[][]> totalPermutations = new ArrayList<>();

        int[][] permutation = new int[3][];
        collectCombinedQuantifiers(groupData, 0, permutation, totalPermutations);

        for (int i = totalPermutations.size() - 1; i >= 0; i--) {
            int[][] permutation2 = totalPermutations.get(i);
            if (checkMatch(permutation2, origin, level)) {

            }
        }

        return null;
    }

    private boolean checkMatch(int[][] quantifierData, BlockPos pos, Level level) {
        for (int i = 0; i < Direction.Axis.values().length; i++) {
            Direction.Axis value = Direction.Axis.values()[i];
            List<Quantifier> quantifiers = this.structureConfiguration.getQuantifiers().get(value);
            int[] quantifierPermutation = quantifierData[i];
            for (int elementIdx = 0; elementIdx < this.structureConfiguration.getSize().get(value); elementIdx++) {

            }
        }
        return false;
    }

    private static int findQuantifierIndex(int offset, List<Quantifier> quantifiers) {
        for (int i = 0; i < quantifiers.size(); i++) {
            Quantifier quantifier = quantifiers.get(i);
            if (quantifier.fromPosition() == offset && quantifier.toPosition() == offset) {
                return i;
            }
        }
        return -1;
    }

    private void collectQuantifiers(List<Integer>[] quantifiers, int idx, int[] permutation, List<int[]> dimensionalPermutations) {
        for (Integer i : quantifiers[idx]) {
            permutation[idx] = i;
            if (idx + 1 < quantifiers.length)
                collectQuantifiers(quantifiers, idx + 1, Arrays.copyOf(permutation, permutation.length), dimensionalPermutations);
            else {
                dimensionalPermutations.add(Arrays.copyOf(permutation, permutation.length));
            }
        }
    }

    private void collectCombinedQuantifiers(List<int[]>[] groupData, int idx, int[][] permutation, List<int[][]> totalPermutations) {
        for (int[] i : groupData[idx]) {
            permutation[idx] = i;
            if (idx + 1 < groupData.length)
                collectCombinedQuantifiers(groupData, idx + 1, Arrays.copyOf(permutation, permutation.length), totalPermutations);
            else {
                totalPermutations.add(Arrays.copyOf(permutation, permutation.length));
            }
        }
    }
}
