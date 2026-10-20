package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace3x1Block;
import net.kapitencraft.kap_lib.multiblock.multiplace.line.MultiplaceLineDirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class Mp3x1TestBlock extends Multiplace3x1Block implements MultiplaceLineDirectionalBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final DirectionProperty FACING = BlockStateProperties.FACING;

    public Mp3x1TestBlock(Properties properties) {
        super(properties.lightLevel(s -> s.getValue(LIT) ? 15 : 0));
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public Direction getDirection(BlockState state) {
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
        super.createBlockStateDefinition(builder);
        builder.add(LIT, FACING);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            boolean flag = state.getValue(LIT);
            if (flag != hasNeighbourSignal(level, pos)) {
                if (flag) {
                    level.scheduleTick(pos, this, 4);
                } else {
                    setProperty(level, pos, LIT, true, 2);
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && !hasNeighbourSignal(level, pos)) {
            setProperty(level, pos, LIT, false, 2);
        }
    }
}