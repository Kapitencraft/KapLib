package net.kapitencraft.kap_lib.multiblock.multiplace.line;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class Multiplace2x1Block extends Block {
    private static final Property<Part> PART = EnumProperty.create("part", Part.class);

    public Multiplace2x1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.LEFT));
    }

    /**
     * @return the direction property to use.
     * <br>one of {@link net.minecraft.world.level.block.state.properties.BlockStateProperties#FACING BSP#FACING}, {@link net.minecraft.world.level.block.state.properties.BlockStateProperties#HORIZONTAL_FACING BSP#HORIZONTAL_FACING} or {@link net.minecraft.world.level.block.state.properties.BlockStateProperties#VERTICAL_DIRECTION BSP#VERTICAL_DIRECTION}
     */
    protected abstract DirectionProperty getDirectionProperty();

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getNeighbourDirection(state.getValue(PART), state.getValue(getDirectionProperty()))) {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        DirectionProperty property = getDirectionProperty();
        Direction direction;
        if (property == BlockStateProperties.FACING) {
            direction = context.getNearestLookingDirection();
        } else if (property == BlockStateProperties.HORIZONTAL_FACING)
            direction = context.getHorizontalDirection();
        else
            direction = context.getNearestLookingVerticalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
                ? this.defaultBlockState().setValue(property, direction)
                : null;
    }

    /**
     * Given a bed part and the direction it's facing, find the direction to move to get the other bed part
     */
    private static Direction getNeighbourDirection(Part part, Direction direction) {
        return part == Part.LEFT ? direction : direction.getOpposite();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(getDirectionProperty()));
            level.setBlock(blockpos, state.setValue(PART, Part.RIGHT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            Part part = state.getValue(PART);
            if (part == Part.LEFT) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(part, state.getValue(getDirectionProperty())));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == Part.RIGHT) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    protected enum Part implements StringRepresentable {
        LEFT,
        RIGHT;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, getDirectionProperty());
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(getDirectionProperty(), rot.rotate(state.getValue(getDirectionProperty())));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(getDirectionProperty())));
    }

}
