package net.kapitencraft.kap_lib.multiblock.structure.match;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.multiblock.MultiblockModuleServerConfig;
import net.kapitencraft.kap_lib.multiblock.structure.MultiblockStructureManager;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;

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
        Vec3i size = structure.getSize();

        Map<Direction.Axis, BlockPattern> sizeGatherer = new EnumMap<>(Direction.Axis.class);
        for (Direction.Axis value : Direction.Axis.values()) {
            int sizeDim = size.get(value);
            sizeGatherer.put(value, BlockPattern.build(structure.getQuantifiers(value), i -> {
                BlockPos pos = BlockPos.ZERO.relative(value, i);
                return structure.getInstanceAt(pos);
            }, sizeDim));
        }
        return new MultiblockStructurePattern(sizeGatherer, structure);
    }

    public MultiblockStructureMatch match(LevelAccessor level, BlockPos origin) {
        List<int[]>[] groupData = new List[3];
        Direction.Axis[] values = Direction.Axis.values();
        for (int i = 0; i < values.length; i++) {
            Direction.Axis value = values[i];
            BlockPattern node = this.sizeAgents.get(value);
            BlockMatcher matcher = node.matcher(level);
            if (!matcher.matches(Direction.fromAxisAndDirection(value, Direction.AxisDirection.POSITIVE), origin))
                return null;
            if (matcher.quantifierList.length > 0) {
                List<Integer>[] quantifiers = matcher.quantifierList;
                List<int[]> dimensionalPermutations = new ArrayList<>();
                int[] permutation = new int[quantifiers.length];
                collectQuantifiers(quantifiers, 0, permutation, dimensionalPermutations);
                groupData[i] = dimensionalPermutations;
            } else {
                groupData[i] = List.of(new int[0]);
            }
        }

        List<int[][]> totalPermutations = new ArrayList<>();

        int[][] permutationData = new int[3][];
        collectCombinedQuantifiers(groupData, 0, permutationData, totalPermutations);
        int volume = 0;
        for (int[][] permutation : totalPermutations) {
            volume += getPermutationSize(permutation).volume();
        }
        if (volume > MultiblockModuleServerConfig.getAsyncPatternMatchingThreshold()) {
            return matchAsync(totalPermutations, origin, level);
        }

        for (int i = totalPermutations.size() - 1; i >= 0; i--) {
            int[][] permutation2 = totalPermutations.get(i);
            if (checkMatch(permutation2, origin, level::getBlockState)) {
                BlockPos permutationSize = getPermutationSize(permutation2);
                return MultiblockStructureMatch.of(level::getBlockState, structureConfiguration, origin, origin.offset(permutationSize));
            }
        }

        return null;
    }

    private BlockPos getPermutationSize(int[][] permutation2) {
        return new BlockPos(getPermutationSize(Direction.Axis.X, permutation2), getPermutationSize(Direction.Axis.Y, permutation2), getPermutationSize(Direction.Axis.Z, permutation2));
    }

    private int getPermutationSize(Direction.Axis axis, int[][] permutations) {
        int value = 0;
        int[] axisPermutations = permutations[axis.ordinal()];
        List<Quantifier> axisQuantifiers = this.structureConfiguration.getQuantifiers(axis);

        for (int i = 0; i < this.structureConfiguration.getSize().get(axis); i++) {
            int q = findQuantifierIndex(i, axisQuantifiers);
            if (q != -1) {
                int length = axisQuantifiers.get(q).toPosition() - axisQuantifiers.get(q).fromPosition() + 1;
                value += axisPermutations[q] * length;
                i = axisQuantifiers.get(q).toPosition();
            } else value++;
        }
        return value;
    }

    private @Nullable MultiblockStructureMatch matchAsync(List<int[][]> totalPermutations, BlockPos pos, LevelAccessor level) {
        //TODO
        //need to collect required level chunk instances before starting the executor, otherwise the thread will deadlock itself

        //no need to do anything if there's nothing to do. quit early to prevent unnecessary work
        if (totalPermutations.isEmpty())
            return null;

        BlockPos maxOffset = null;

        List<Pair<int[][], Integer>> permutationVolumeData = new ArrayList<>();
        for (int[][] permutation : totalPermutations) {
            BlockPos permutationSize = getPermutationSize(permutation);
            if (maxOffset == null) {
                maxOffset = permutationSize;
            } else {
                maxOffset = BlockPos.max(maxOffset, permutationSize);
            }
            permutationVolumeData.add(Pair.of(permutation, permutationSize.volume()));
        }
        permutationVolumeData.sort(Comparator.comparingInt(Pair::getSecond));

        AsyncChunkCache asyncChunkCache = AsyncChunkCache.create(level, pos, maxOffset);

        ExecutorService executor = Executors.newFixedThreadPool(10, new PatternMatcherThreadFactory());

        try {
            AtomicInteger matchedVolume = new AtomicInteger();
            AtomicReference<MultiblockStructureMatch> objRef = new AtomicReference<>();
            List<CompletableFuture<MultiblockStructureMatch>> futures = new ArrayList<>();
            for (int i = permutationVolumeData.size() - 1; i >= 0; i--) {
                Pair<int[][], Integer> value = permutationVolumeData.get(i);
                int[][] permutations = value.getFirst();
                futures.add(CompletableFuture.supplyAsync(() -> {
                    //ensure a match is only checked for if there isn't a larger one already confirmed
                    if (matchedVolume.get() < value.getSecond()) {
                        if (checkMatch(permutations, pos, asyncChunkCache::getBlockState)) {
                            BlockPos size = getPermutationSize(permutations);
                            return MultiblockStructureMatch.of(asyncChunkCache::getBlockState, this.structureConfiguration, pos, pos.offset(size));
                        }
                    }
                    return null;
                }, executor).whenComplete((u, throwable) -> {
                    if (throwable != null) {
                        MultiblockStructureManager.LOGGER.warn("error matching multiblock structure: {}", throwable.getMessage());
                    }
                    if (u != null && matchedVolume.get() < value.getSecond()) {
                        objRef.set(u);
                        matchedVolume.set(value.getSecond());
                    }
                }));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            return objRef.get();
        } finally {
            executor.shutdownNow();
        }
    }

    private static class PatternMatcherThreadFactory implements ThreadFactory {
        AtomicInteger id = new AtomicInteger();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "MultiblockStructurePatternMatchWorker#" + id.incrementAndGet());
        }
    }

    private boolean checkMatch(int[][] quantifierData, BlockPos pos, Function<BlockPos, BlockState> stateGetter) {
        //<dim> = offset of the checked position in the world
        int x = 0;
        //quantifiers = quantifiers for the given dimension
        List<Quantifier> quantifiersX = this.structureConfiguration.getQuantifiers(Direction.Axis.X);
        //quantifierPermutation = repetitions per group, indexed in the same order then the quantifiers
        int[] quantifierPermutationX = quantifierData[0];
        int sizeX = this.structureConfiguration.getSize().get(Direction.Axis.X);
        //elementIdx = idx into the structure configuration spacial data
        for (int elementIdxX = 0; elementIdxX < sizeX; ) {

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
                    List<Quantifier> quantifiersY = this.structureConfiguration.getQuantifiers(Direction.Axis.Y);
                    int[] quantifierPermutationY = quantifierData[1];
                    int sizeY = this.structureConfiguration.getSize().get(Direction.Axis.Y);
                    for (int elementIdxY = 0; elementIdxY < sizeY; ) {

                        int qEIdxY = findQuantifierIndex(elementIdxY, quantifiersY);

                        int countY = qEIdxY != -1 ? quantifierPermutationY[qEIdxY] : 1;
                        //length of the quantifier
                        int sectionLengthY = qEIdxY != -1 ? quantifiersY.get(qEIdxY).toPosition() - elementIdxY + 1 : 1;

                        for (int iY = 0; iY < countY; iY++) {

                            for (int sIdxY = 0; sIdxY < sectionLengthY; sIdxY++) {
                                int z = 0;
                                List<Quantifier> quantifiersZ = this.structureConfiguration.getQuantifiers(Direction.Axis.Z);
                                int[] quantifierPermutationZ = quantifierData[2];

                                int sizeZ = this.structureConfiguration.getSize().get(Direction.Axis.Z);
                                for (int elementIdxZ = 0; elementIdxZ < sizeZ; ) {

                                    int qEIdxZ = findQuantifierIndex(elementIdxZ, quantifiersZ);

                                    int countZ = qEIdxZ != -1 ? quantifierPermutationZ[qEIdxZ] : 1;
                                    //length of the quantifier
                                    int sectionLengthZ = qEIdxZ != -1 ? quantifiersZ.get(qEIdxZ).toPosition() - elementIdxZ + 1 : 1;

                                    for (int iZ = 0; iZ < countZ; iZ++) {

                                        for (int sIdxZ = 0; sIdxZ < sectionLengthZ; sIdxZ++) {
                                            if (!this.structureConfiguration.getInstanceAt(elementIdxX + sIdxX, elementIdxY + sIdxY, elementIdxZ + sIdxZ).isValid(stateGetter.apply(pos.offset(x, y, z))))
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

    public Multimap<BlockState, Vec3i> buildInfo() {
        return this.structureConfiguration.buildInfo();
    }

    @NotNull
    public List<BlockPos> findPotentialOrigins(LevelAccessor level, BlockPos pos, Vec3i posInStructure) {
        List<Integer>[] dimensionalPosOffsets = new List[3];
        for (Direction.Axis value : Direction.Axis.values()) {
            List<Quantifier> quantifiers = this.structureConfiguration.getQuantifiers(value);
            List<Integer> positionOffsets = new ArrayList<>();
            searchPotentialOrigins(posInStructure.get(value), 0, (structureIndex, worldPositionOffset) ->
                    structureConfiguration.getInstanceAt(
                            posInStructure.relative(value, structureIndex - posInStructure.get(value)))
                            .isValid(level.getBlockState(pos.relative(value, worldPositionOffset))),
                    quantifiers, positionOffsets
            );
            //no potential offset found for the given axis, returning
            if (positionOffsets.isEmpty())
                return List.of();
            dimensionalPosOffsets[value.ordinal()] = positionOffsets;
        }

        List<int[]> offsets = new ArrayList<>();
        int[] values = new int[3];
        collectQuantifiers(dimensionalPosOffsets, 0, values, offsets);

        List<BlockPos> origins = new ArrayList<>();
        for (int[] offset : offsets) {
            BlockPos origin = pos.offset(offset[0], offset[1], offset[2]);
            if (!structureConfiguration.getInstanceAt(0, 0, 0).isValid(level.getBlockState(origin)))
                continue;

            origins.add(origin);
        }

        return origins;
    }

    private static void searchPotentialOrigins(int structureIndex, int worldPositionOffset, BiPredicate<Integer, Integer> blockValidityCheck, List<Quantifier> quantifiers, List<Integer> results) {
        if (structureIndex < 0) {
            //end reached. save size and return
            results.add(worldPositionOffset + 1);
            return;
        }
        Quantifier q = findQuantifierInRange(quantifiers, structureIndex);
        if (q != null) {
            while (structureIndex > q.fromPosition()) {
                structureIndex--;
                worldPositionOffset--;
                if (!blockValidityCheck.test(structureIndex, worldPositionOffset)) {
                    //quantifier doesn't match.
                    return;
                }
            }
            worldPositionOffset--;
            //successfully searched through the entire quantifier.
            //check if there's more repetitions and check if the match is successful without
            searchPotentialOrigins(structureIndex - 1, worldPositionOffset, blockValidityCheck, quantifiers, results);
            //one for the exit from the current iteration
            for (int quantifierIterations = 1; q.maxCount() == -1 || quantifierIterations <= q.maxCount(); quantifierIterations++) {
                for (structureIndex = q.toPosition(); structureIndex >= q.fromPosition(); structureIndex--) {
                    if (!blockValidityCheck.test(structureIndex, worldPositionOffset)) {
                        //quantifier doesn't match again
                        return;
                    }

                    worldPositionOffset--;
                }
                searchPotentialOrigins(structureIndex, worldPositionOffset, blockValidityCheck, quantifiers, results);
            }
        } else {
            if (blockValidityCheck.test(structureIndex, worldPositionOffset))
                searchPotentialOrigins(structureIndex - 1, worldPositionOffset - 1, blockValidityCheck, quantifiers, results);
        }
    }

    @Nullable
    private static Quantifier findQuantifierInRange(List<Quantifier> quantifiers, int idx) {
        for (Quantifier quantifier : quantifiers) {
            if (quantifier.fromPosition() <= idx && quantifier.toPosition() >= idx) {
                return quantifier;
            }
        }
        return null;
    }
}
