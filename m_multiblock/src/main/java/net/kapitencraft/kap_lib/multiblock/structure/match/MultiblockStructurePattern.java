package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
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

    public MultiblockStructureMatch match(ServerLevel level, BlockPos origin) {
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

        int[][] permutationData = new int[3][];
        collectCombinedQuantifiers(groupData, 0, permutationData, totalPermutations);

        for (int i = totalPermutations.size() - 1; i >= 0; i--) {
            int[][] permutation2 = totalPermutations.get(i);
            if (checkMatch(permutation2, origin, level)) {
                BlockPos permutationSize = getPermutationSize(permutation2);
                return MultiblockStructureMatch.of(level, origin, origin.offset(permutationSize));
            }
        }

        return null;
    }

    private BlockPos getPermutationSize(int[][] permutation2) {
        Vec3i configurationSize = this.structureConfiguration.getSize();

        int x = 0;
        int[] permutationsX = permutation2[0];
        List<Quantifier> quantifiersX = this.structureConfiguration.getQuantifiers().get(Direction.Axis.X);

        for (int i = 0; i < configurationSize.getX(); i++) {
            int q = findQuantifierIndex(i, quantifiersX);
            if (q != -1) {
                int length = quantifiersX.get(q).toPosition() - quantifiersX.get(q).fromPosition() + 1;
                x += permutationsX[q] * length;
                i = quantifiersX.get(q).toPosition();
            } else
                x++;

        }

        int y = 0;
        int[] permutationsY = permutation2[1];
        List<Quantifier> quantifiersY = this.structureConfiguration.getQuantifiers().get(Direction.Axis.Y);

        for (int i = 0; i < configurationSize.getY(); i++) {
            int q = findQuantifierIndex(y, quantifiersY);
            if (q != -1) {
                int length = quantifiersY.get(q).toPosition() - quantifiersY.get(q).fromPosition() + 1;
                y += permutationsY[q] * length;
                i = quantifiersY.get(q).toPosition();
            } else
                y++;
        }

        int z = 0;
        int[] permutationsZ = permutation2[2];
        List<Quantifier> quantifiersZ = this.structureConfiguration.getQuantifiers().get(Direction.Axis.Z);

        for (int i = 0; i < configurationSize.getZ(); i++) {
            int q = findQuantifierIndex(z, quantifiersZ);
            if (q != -1) {
                int length = quantifiersZ.get(q).toPosition() - quantifiersZ.get(q).fromPosition() + 1;
                z += permutationsZ[q] * length;
                i = quantifiersZ.get(q).toPosition();
            } else
                z++;
        }

        return new BlockPos(x, y, z);
    }

    private boolean checkMatch(int[][] quantifierData, BlockPos pos, Level level) {
        //<dim> = offset of the checked position in the world
        int x = 0;
        //quantifiers = quantifiers for the given dimension
        List<Quantifier> quantifiersX = this.structureConfiguration.getQuantifiers().get(Direction.Axis.X);
        //quantifierPermutation = repetitions per group, indexed in the same order than the quantifiers
        int[] quantifierPermutationX = quantifierData[0];
        int sizeX = this.structureConfiguration.getSize().get(Direction.Axis.X);
        //elementIdx = idx into the structure configuration spacial data
        for (int elementIdxX = 0; elementIdxX < sizeX;) {

            //qEIdx = idx of the currently used index
            int qEIdxX = findQuantifierIndex(elementIdxX, quantifiersX);
            //count = repetitions of the quantifier, or one if there's no quantifier
            int countX = qEIdxX != -1 ? quantifierPermutationX[qEIdxX] : 1;
            //length of the quantifier
            int sectionLengthX = qEIdxX != -1 ? quantifiersX.get(qEIdxX).toPosition() - elementIdxX + 1 : 1;
            //i = current repetition index
            for (int iX = 0; iX < countX; iX++) {

                //sIdx = current offset inside the repetition
                for (int sIdxX = 0; sIdxX < sectionLengthX; sIdxX++) {
                    int y = 0;
                    List<Quantifier> quantifiersY = this.structureConfiguration.getQuantifiers().get(Direction.Axis.Y);
                    int[] quantifierPermutationY = quantifierData[1];
                    int sizeY = this.structureConfiguration.getSize().get(Direction.Axis.Y);
                    for (int elementIdxY = 0; elementIdxY < sizeY;) {

                        int qEIdxY = findQuantifierIndex(elementIdxY, quantifiersY);

                        int countY = qEIdxY != -1 ? quantifierPermutationY[qEIdxY] : 1;
                        //length of the quantifier
                        int sectionLengthY = qEIdxY != -1 ? quantifiersY.get(qEIdxY).toPosition() - elementIdxY + 1 : 1;

                        for (int iY = 0; iY < countY; iY++) {

                            for (int sIdxY = 0; sIdxY < sectionLengthY; sIdxY++) {
                                int z = 0;
                                List<Quantifier> quantifiersZ = this.structureConfiguration.getQuantifiers().get(Direction.Axis.Z);
                                int[] quantifierPermutationZ = quantifierData[2];

                                int sizeZ = this.structureConfiguration.getSize().get(Direction.Axis.Z);
                                for (int elementIdxZ = 0; elementIdxZ < sizeZ;) {

                                    int qEIdxZ = findQuantifierIndex(elementIdxZ, quantifiersZ);

                                    int countZ = qEIdxZ != -1 ? quantifierPermutationZ[qEIdxZ] : 1;
                                    //length of the quantifier
                                    int sectionLengthZ = qEIdxZ != -1 ? quantifiersZ.get(qEIdxZ).toPosition() - elementIdxZ + 1 : 1;

                                    for (int iZ = 0; iZ < countZ; iZ++) {

                                        for (int sIdxZ = 0; sIdxZ < sectionLengthZ; sIdxZ++) {
                                            if (!this.structureConfiguration.getInstanceAt(
                                                    elementIdxX + sIdxX,
                                                    elementIdxY + sIdxY,
                                                    elementIdxZ + sIdxZ
                                            ).isValid(level.getBlockState(pos.offset(x, y, z))))
                                                return false;
                                            z++;
                                        }
                                    }
                                    elementIdxZ += sectionLengthZ;
                                }
                                y++;
                            }
                        }
                        elementIdxY += sectionLengthY;
                    }
                    x++;
                }
            }
            elementIdxX += sectionLengthX;
        }
        return true;
    }

    private static int findQuantifierIndex(int offset, List<Quantifier> quantifiers) {
        for (int i = 0; i < quantifiers.size(); i++) {
            Quantifier quantifier = quantifiers.get(i);
            if (quantifier.fromPosition() == offset && quantifier.toPosition() >= offset) {
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
