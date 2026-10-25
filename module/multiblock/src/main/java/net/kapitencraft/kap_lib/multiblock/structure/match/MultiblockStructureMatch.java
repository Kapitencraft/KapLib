package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MultiblockStructureMatch {

    private final BlockState[][][] states;
    private final Vec3i size;
    private final MultiblockStructureConfiguration configuration;

    public MultiblockStructureMatch(BlockState[][][] states, Vec3i size, MultiblockStructureConfiguration configuration) {
        this.states = states;
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

    public Vec3i getSize() {
        return this.size;
    }

    public static MultiblockStructureMatch of(LevelAccessor level, MultiblockStructureConfiguration configuration, BlockPos start, BlockPos end) {
        BlockPos size = end.subtract(start);
        BlockState[][][] states = new BlockState[size.getX()][size.getY()][size.getZ()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    states[x][y][z] = level.getBlockState(start.offset(x,y,z));
                }
            }
        }
        return new MultiblockStructureMatch(states, size, configuration);
    }
}