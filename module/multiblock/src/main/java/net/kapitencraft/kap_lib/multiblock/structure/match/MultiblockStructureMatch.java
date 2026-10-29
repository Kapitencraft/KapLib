package net.kapitencraft.kap_lib.multiblock.structure.match;

import com.google.common.collect.ImmutableMap;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MultiblockStructureMatch {

    private final BlockState[][][] states;
    private final Map<String, Integer> namedQuantifierSizes;
    private final Vec3i size;
    private final MultiblockStructureConfiguration configuration;

    private MultiblockStructureMatch(BlockState[][][] states, Map<String, Integer> namedQuantifierSizes, Vec3i size, MultiblockStructureConfiguration configuration) {
        this.states = states;
        this.namedQuantifierSizes = namedQuantifierSizes;
        this.size = size;
        this.configuration = configuration;
    }

    @Nullable
    public BlockState getStateAt(BlockPos pos) {
        return states[pos.getX()][pos.getY()][pos.getZ()];
    }

    public BlockState getStateAt(int x, int y, int z) {
        return states[x][y][z];
    }

    public int getQuantifier(String name) {
        return namedQuantifierSizes.get(name);
    }

    public Vec3i getSize() {
        return this.size;
    }

    public static MultiblockStructureMatch of(Function<BlockPos, BlockState> stateGetter, int[][] permutation, MultiblockStructureConfiguration configuration, BlockPos start, BlockPos end) {
        BlockPos size = end.subtract(start);
        BlockState[][][] states = new BlockState[size.getX()][size.getY()][size.getZ()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    states[x][y][z] = stateGetter.apply(start.offset(x, y, z));
                }
            }
        }

        ImmutableMap.Builder<String, Integer> builder = new ImmutableMap.Builder<>();
        for (Direction.Axis value : Direction.Axis.values()) {
            collectNamedQuantifier(builder, configuration, permutation, value);
        }
        return new MultiblockStructureMatch(states, builder.build(), size, configuration);
    }

    private static void collectNamedQuantifier(ImmutableMap.Builder<String, Integer> sink, MultiblockStructureConfiguration configuration, int[][] permutation, Direction.Axis axis) {
        List<Quantifier> quantifiers = configuration.getQuantifiers(axis);
        for (int i = 0; i < quantifiers.size(); i++) {
            Quantifier quantifier = quantifiers.get(i);
            if (!quantifier.name().isEmpty()) {
                sink.put(quantifier.name(), permutation[axis.ordinal()][i]);
            }
        }
    }

}