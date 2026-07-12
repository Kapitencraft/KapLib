package net.kapitencraft.kap_lib.multiblock.multiplace.line;

import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.multiblock.multiplace.MultiplaceBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class Multiplace2x1Block extends Block implements MultiplaceBlock {
    private static final Property<Part> PART = EnumProperty.create("part", Part.class);

    public Multiplace2x1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.LEFT));
    }

    protected abstract @NotNull Direction getDirection(BlockState state);

    protected abstract Direction determinPlaceDirection(BlockPlaceContext context);

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getNeighbourDirection(state.getValue(PART), getDirection(state))) {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    /**
     * allows for implementation of direction properties on the Multiplace Block
     *
     * @param state     the state to be applied to
     * @param direction the direction to be applied
     * @return the state with applied direction
     */
    protected BlockState setDirectionOnState(BlockState state, Direction direction) {
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = determinPlaceDirection(context);
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
                ? setDirectionOnState(this.defaultBlockState(), direction)
                : null;
    }

    @Override
    public <T extends Comparable<T>> void setProperty(Level level, BlockPos pos, Property<T> property, T value, int flags) {
        BlockState state = level.getBlockState(pos);
        Part part = state.getValue(PART);
        Direction direction = getDirection(state);
        MiscHelper.updateState(level, pos, property, value, flags);
        switch (part) {
            case LEFT -> {
                MiscHelper.updateState(level, pos.relative(direction), property, value, flags);
            }
            case RIGHT -> {
                MiscHelper.updateState(level, pos.relative(direction.getOpposite()), property, value, flags);
            }
        }
    }

    protected boolean hasNeighbourSignal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        return level.hasNeighborSignal(pos) ||
                level.hasNeighborSignal(pos.relative(state.getValue(PART) == Part.LEFT ?
                        getDirection(state) :
                        getDirection(state).getOpposite()
                ));
    }

    protected int getSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getSignal(pos, direction);
        }
        BlockPos other = pos.relative(state.getValue(PART) == Part.LEFT ?
                getDirection(state) :
                getDirection(state).getOpposite()
        );
        return level.getSignal(other, direction);
    }

    protected int getDirectSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getDirectSignal(pos.relative(direction), direction);
        }
        BlockPos other = pos.relative(state.getValue(PART) == Part.LEFT ?
                getDirection(state) :
                getDirection(state).getOpposite()
        );
        return level.getDirectSignal(other.relative(direction), direction);
    }

    protected int getAnalogSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            BlockPos targetLoc = pos.relative(direction);
            return level.getBlockState(targetLoc).getAnalogOutputSignal(level, targetLoc);
        }
        BlockPos other = pos.relative(state.getValue(PART) == Part.LEFT ?
                getDirection(state) :
                getDirection(state).getOpposite()
        ).relative(direction);
        return level.getBlockState(other).getAnalogOutputSignal(level, other);
    }

    /**
     * Given a bed part and the direction it's facing, find the direction to move to get the other bed part
     */
    private static Direction getNeighbourDirection(Part part, Direction direction) {
        return part == Part.LEFT ? direction : direction.getOpposite();
    }

    public BlockPos getOriginPositionFromState(BlockState state, BlockPos pos) {
        return isOrigin(state) ? pos : pos.relative(getDirection(state));
    }

    @Override
    public boolean isOrigin(BlockState state) {
        return state.getValue(PART) == Part.LEFT;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(getDirection(state));
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
                BlockPos blockpos = pos.relative(getNeighbourDirection(part, getDirection(state)));
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
        builder.add(PART);
        super.createBlockStateDefinition(builder);
    }
}
