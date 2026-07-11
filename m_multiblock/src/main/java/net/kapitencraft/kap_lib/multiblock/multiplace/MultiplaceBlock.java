package net.kapitencraft.kap_lib.multiblock.multiplace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface MultiplaceBlock {

    boolean isOrigin(BlockState state);

    BlockPos getOriginPositionFromState(BlockState state, BlockPos pos);
}
