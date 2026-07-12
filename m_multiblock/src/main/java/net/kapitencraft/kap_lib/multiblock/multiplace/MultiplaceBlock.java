package net.kapitencraft.kap_lib.multiblock.multiplace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface MultiplaceBlock {

    boolean isOrigin(BlockState state);

    BlockPos getOriginPositionFromState(BlockState state, BlockPos pos);

    <T extends Comparable<T>> void setProperty(Level level, BlockPos pos, Property<T> property, T value, int flags);
}
