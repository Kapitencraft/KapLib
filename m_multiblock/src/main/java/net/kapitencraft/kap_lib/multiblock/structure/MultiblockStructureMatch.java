package net.kapitencraft.kap_lib.multiblock.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockStructureMatch {

    private final BlockState[][][] states;

    public MultiblockStructureMatch(BlockState[][][] states) {
        this.states = states;
    }

    public BlockState getStateAt(BlockPos pos) {
        return states[pos.getX()][pos.getY()][pos.getZ()];
    }

    public BlockState getStateAt(int x, int y, int z) {
        return states[x][y][z];
    }

    public static MultiblockStructureMatch of(ServerLevel level, BlockPos start, BlockPos end) {
        BlockPos size = end.subtract(start);
        BlockState[][][] states = new BlockState[size.getX()][size.getY()][size.getZ()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    states[x][y][z] = level.getBlockState(start.offset(x,y,z));
                }
            }
        }
        return new MultiblockStructureMatch(states);
    }
}