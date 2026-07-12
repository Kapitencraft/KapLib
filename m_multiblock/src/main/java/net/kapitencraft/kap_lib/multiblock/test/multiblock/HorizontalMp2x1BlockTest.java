package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace2x1Block;
import net.kapitencraft.kap_lib.multiblock.multiplace.line.MultiplaceLineDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class HorizontalMp2x1BlockTest extends Multiplace2x1Block implements MultiplaceLineDirectionalBlock {
    public HorizontalMp2x1BlockTest(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getDirection(BlockState state) {
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected Direction determinPlaceDirection(BlockPlaceContext context) {
        return context.getHorizontalDirection();
    }

    @Override
    public DirectionProperty getDirectionProperty() {
        return BlockStateProperties.HORIZONTAL_FACING;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return MultiplaceLineDirectionalBlock.super.rotate(state, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return MultiplaceLineDirectionalBlock.super.mirror(state, mirror);
    }
}
