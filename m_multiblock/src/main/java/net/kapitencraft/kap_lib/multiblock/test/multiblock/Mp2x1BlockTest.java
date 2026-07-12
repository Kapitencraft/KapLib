package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace2x1Block;
import net.kapitencraft.kap_lib.multiblock.multiplace.line.MultiplaceLineDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public class Mp2x1BlockTest extends Multiplace2x1Block implements MultiplaceLineDirectionalBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public Mp2x1BlockTest(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Direction getDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public DirectionProperty getDirectionProperty() {
        return FACING;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return MultiplaceLineDirectionalBlock.super.rotate(state, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return MultiplaceLineDirectionalBlock.super.mirror(state, mirror);
    }

    @Override
    protected Direction determinPlaceDirection(BlockPlaceContext context) {
        return context.getNearestLookingDirection();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }
}
