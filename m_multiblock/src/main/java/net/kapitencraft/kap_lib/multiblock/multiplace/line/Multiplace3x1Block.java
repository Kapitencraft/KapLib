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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class Multiplace3x1Block extends Block implements MultiplaceBlock {
    private static final Property<Part> PART = EnumProperty.create("part", Part.class);

    public Multiplace3x1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.LEFT));
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
                MiscHelper.updateState(level, pos.relative(direction, 2), property, value, flags);
            }
            case MIDDLE -> {
                MiscHelper.updateState(level, pos.relative(direction.getOpposite()), property, value, flags);
                MiscHelper.updateState(level, pos.relative(direction), property, value, flags);
            }
            case RIGHT -> {
                Direction opposite = direction.getOpposite();
                MiscHelper.updateState(level, pos.relative(opposite), property, value, flags);
                MiscHelper.updateState(level, pos.relative(opposite, 2), property, value, flags);
            }
        }
    }

    protected boolean hasNeighbourSignal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (level.hasNeighborSignal(pos))
            return true;

        Direction direction = getDirection(state);
        return switch (state.getValue(PART)) {
            case LEFT -> level.hasNeighborSignal(pos.relative(direction)) ||
                    level.hasNeighborSignal(pos.relative(direction, 2));
            case MIDDLE -> level.hasNeighborSignal(pos.relative(direction)) ||
                    level.hasNeighborSignal(pos.relative(direction.getOpposite()));
            case RIGHT -> {
                Direction opposite = direction.getOpposite();
                yield level.hasNeighborSignal(pos.relative(opposite)) ||
                        level.hasNeighborSignal(pos.relative(opposite, 2));
            }
        };
    }

    protected abstract @NotNull Direction getDirection(BlockState state);

    protected abstract Direction determinPlaceDirection(BlockPlaceContext context);

    protected int getSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getSignal(pos, direction);
        }
        Direction mbDirection = getDirection(state);
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }
        return level.getSignal(other.relative(direction), direction);
    }

    protected int getDirectSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getDirectSignal(pos.relative(direction), direction);
        }
        Direction mbDirection = getDirection(state);
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }

        return level.getDirectSignal(other.relative(direction), direction);
    }

    protected int getAnalogSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            BlockPos targetLoc = pos.relative(direction);
            return level.getBlockState(targetLoc).getAnalogOutputSignal(level, targetLoc);
        }
        Direction mbDirection = getDirection(state);
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }
        BlockPos position = other.relative(direction);
        return level.getBlockState(position).getAnalogOutputSignal(level, position);
    }

    @Override
    public <T extends Comparable<T>> void setProperty(Level level, BlockPos pos, Property<T> property, T value, int flags) {
        BlockState state = level.getBlockState(pos);
        Part part = state.getValue(PART);
        Direction direction = state.getValue(getDirectionProperty());
        MiscHelper.updateState(level, pos, property, value, flags);
        switch (part) {
            case LEFT -> {
                MiscHelper.updateState(level, pos.relative(direction), property, value, flags);
                MiscHelper.updateState(level, pos.relative(direction, 2), property, value, flags);
            }
            case MIDDLE -> {
                MiscHelper.updateState(level, pos.relative(direction.getOpposite()), property, value, flags);
                MiscHelper.updateState(level, pos.relative(direction), property, value, flags);
            }
            case RIGHT -> {
                Direction opposite = direction.getOpposite();
                MiscHelper.updateState(level, pos.relative(opposite), property, value, flags);
                MiscHelper.updateState(level, pos.relative(opposite, 2), property, value, flags);
            }
        }
    }

    protected boolean hasNeighbourSignal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (level.hasNeighborSignal(pos))
            return true;

        Direction direction = state.getValue(getDirectionProperty());
        return switch (state.getValue(PART)) {
            case LEFT -> level.hasNeighborSignal(pos.relative(direction)) ||
                    level.hasNeighborSignal(pos.relative(direction, 2));
            case MIDDLE -> level.hasNeighborSignal(pos.relative(direction)) ||
                    level.hasNeighborSignal(pos.relative(direction.getOpposite()));
            case RIGHT -> {
                Direction opposite = direction.getOpposite();
                yield level.hasNeighborSignal(pos.relative(opposite)) ||
                        level.hasNeighborSignal(pos.relative(opposite, 2));
            }
        };
    }

    protected int getSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getSignal(pos, direction);
        }
        Direction mbDirection = state.getValue(getDirectionProperty());
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }
        return level.getSignal(other.relative(direction), direction);
    }

    protected int getDirectSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            return level.getDirectSignal(pos.relative(direction), direction);
        }
        Direction mbDirection = state.getValue(getDirectionProperty());
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }

        return level.getDirectSignal(other.relative(direction), direction);
    }

    protected int getAnalogSignal(Level level, BlockPos pos, Part part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(PART) == part) {
            BlockPos targetLoc = pos.relative(direction);
            return level.getBlockState(targetLoc).getAnalogOutputSignal(level, targetLoc);
        }
        Direction mbDirection = state.getValue(getDirectionProperty());
        BlockPos other = BlockPos.ZERO;
        switch (state.getValue(PART)) {
            case LEFT -> {
                if (part == Part.MIDDLE)
                    other = pos.relative(mbDirection);
                else
                    other = pos.relative(mbDirection, 2);
            }
            case MIDDLE -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection);
            }
            case RIGHT -> {
                if (part == Part.LEFT)
                    other = pos.relative(mbDirection.getOpposite());
                else
                    other = pos.relative(mbDirection.getOpposite(), 2);
            }
        }
        BlockPos position = other.relative(direction);
        return level.getBlockState(position).getAnalogOutputSignal(level, position);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction bFacing = getDirection(state);
        Direction oFacing = bFacing.getOpposite();
        Part part = state.getValue(PART);
        if ((part != Part.LEFT && facing == oFacing) || (part != Part.RIGHT && facing == bFacing)) {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    /**
     * allows for implementation of direction properties on the Multiplace Block
     * @param state the state to be applied to
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
        BlockPos clickedPos = context.getClickedPos();
        BlockPos middlePos = clickedPos.relative(direction);
        BlockPos rightPos = clickedPos.relative(direction, 2);
        Level level = context.getLevel();

        return level.getBlockState(middlePos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(middlePos)
                && level.getBlockState(rightPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(rightPos)
                ? setDirectionOnState(this.defaultBlockState(), direction)
                : null;
    }

    /**
     * Given a bed part and the direction it's facing, find the direction to move to get the other bed part
     */
    private static Direction getNeighbourDirection(Part part, Direction direction) {
        return part == Part.LEFT ? direction : direction.getOpposite();
    }

    public BlockPos getOriginPositionFromState(BlockState state, BlockPos pos) {
        Part part = state.getValue(PART);
        return part == Part.LEFT ? pos : pos.relative(getDirection(state), part == Part.MIDDLE ? 1 : 2);
    }

    @Override
    public boolean isOrigin(BlockState state) {
        return state.getValue(PART) == Part.LEFT;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            {
                BlockPos blockpos = pos.relative(getDirection(state));
                level.setBlock(blockpos, state.setValue(PART, Part.MIDDLE), 3);
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, 3);
            }

            BlockPos blockpos = pos.relative(getDirection(state), 2);
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
                {
                    BlockPos blockpos = pos.relative(getNeighbourDirection(part, getDirection(state)));
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.is(this) && blockstate.getValue(PART) == Part.MIDDLE) {
                        level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                        level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                    }
                }

                BlockPos blockpos = pos.relative(getNeighbourDirection(part, getDirection(state)), 2);
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
        MIDDLE,
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
