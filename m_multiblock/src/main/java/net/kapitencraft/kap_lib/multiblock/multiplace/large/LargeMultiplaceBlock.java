package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.multiblock.multiplace.MultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.MultiblockOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.Orientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public abstract class LargeMultiplaceBlock<O extends MultiblockOrientation<O>, P extends MultiplaceBlockPart<P>> extends Block implements MultiplaceBlock {
    //DOWN -> NORTH, WEST
    //UP   -> SOUTH, EAST
    //NORTH-> WEST, DOWN
    //SOUTH->

    private final P origin;

    /**
     * @param properties the BlockProperties of the block
     * @param origin the origin of the multiplace property. must always be ordinal 0 of the available parts
     */
    public LargeMultiplaceBlock(Properties properties, P origin) {
        super(properties);
        this.origin = origin;
        this.registerDefaultState(this.stateDefinition.any().setValue(getPartProperty(), origin));
    }

    /**
     * @return the direction property to use. this
     * <br>one of {@link Orientation#PROPERTY}, {@link HorizontalOrientation#PROPERTY}
     */
    protected abstract Property<O> getOrientationProperty();

    /**
     * @return the part property to use. must accept the same values as returned in {@link #getPartValues()}
     */
    protected abstract Property<P> getPartProperty();

    @Override
    protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        O orientation = state.getValue(getOrientationProperty());

        Property<P> partProperty = getPartProperty();
        P part = state.getValue(partProperty);
        BlockPos origin = currentPos;
        if (part != this.origin) {
            origin = origin.subtract(orientation.getPos(part));
        }
        P expectedPart = getPartForOffset(orientation, origin, facingPos);
        if (expectedPart != null) {
            return facingState.is(this) && facingState.getValue(partProperty) == expectedPart
                    ? state
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    /**
     * attempts to get the part for the given offset
     * @param orientation the orientation of the multiplace block instance to query
     * @param origin the position of the origin block for the multiplace instance
     * @param offset the offset of the block to check
     * @return the part for the offset or null, if it isn't part of the multiplace instance
     */
    protected @Nullable P getPartForOffset(O orientation, BlockPos origin, BlockPos offset) {
        BlockPos relativeOffset = offset.subtract(origin);
        for (P part : this.getPartValues()) {
            if (orientation.getPos(part).equals(relativeOffset)) {
                return part;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Property<O> property = getOrientationProperty();
        O value = MultiblockOrientation.getOrientation(context, property);
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        WorldBorder worldBorder = level.getWorldBorder();
        for (P part : getPartValues()) {
            BlockPos b = value.getPos(part).offset(blockpos);
            if (!level.getBlockState(b).canBeReplaced(context) || !worldBorder.isWithinBounds(b))
                return null;
        }
        return this.defaultBlockState().setValue(property, value);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            O orientation = state.getValue(getOrientationProperty());
            P[] values = getPartValues();
            for (int i = 1; i < values.length; i++) { //skip origin
                P part = values[i];
                BlockPos blockpos = orientation.getPos(part).offset(pos);
                level.setBlock(blockpos, state.setValue(getPartProperty(), part), 3);
                //level.blockUpdated(blockpos, Blocks.AIR);
                //state.updateNeighbourShapes(level, blockpos, 3);
            }
        }
    }

    /**
     * provides the parts this multiplace block is composed of.
     * must return the origin as its first value
     * @return all parts the multiplace block is composed of. must always return the same values
     */
    protected abstract P[] getPartValues();

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative()) {
            P part = state.getValue(getPartProperty());
            O orientation = state.getValue(getOrientationProperty());
            if (part != origin) {
                pos = pos.subtract(orientation.getPos(part));
            }
            for (P value : getPartValues()) {
                if (value != part) {
                    BlockPos blockpos = orientation.getPos(value).offset(pos);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.is(this)) {
                        level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                        level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getPartProperty(), getOrientationProperty());
        super.createBlockStateDefinition(builder);
    }

    /**
     * returns the position of the BE
     */
    protected BlockPos getOriginPosition(O orientation, P part, BlockPos current) {
        return current.subtract(orientation.getPos(part));
    }

    public BlockPos getOriginPositionFromState(BlockState state, BlockPos pos) {
        return getOriginPosition(state.getValue(getOrientationProperty()), state.getValue(getPartProperty()), pos);
    }

    public boolean isOrigin(BlockState state) {
        return state.getValue(getPartProperty()) == origin;
    }

    @Override
    public <T extends Comparable<T>> void setProperty(Level level, BlockPos pos, Property<T> property, T value, int flags) {
        BlockState state = level.getBlockState(pos);
        BlockPos origin = getOriginPositionFromState(state, pos);
        O orientation = state.getValue(getOrientationProperty());
        for (P p : this.getPartValues()) {
            MiscHelper.updateState(level, origin.offset(orientation.getPos(p)), property, value, flags);
        }
    }

    protected boolean hasNeighbourSignal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (level.hasNeighborSignal(pos))
            return true;

        BlockPos origin = getOriginPositionFromState(state, pos);
        O orientation = state.getValue(getOrientationProperty());
        for (P value : this.getPartValues()) {
            if (level.hasNeighborSignal(origin.offset(orientation.getPos(value)))) {
                return true;
            }
        }
        return false;
    }

    protected int getSignal(Level level, BlockPos pos, P part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(getPartProperty()) == part) {
            return level.getSignal(pos.relative(direction), direction);
        }
        O orientation = state.getValue(getOrientationProperty());
        BlockPos origin = getOriginPositionFromState(state, pos);
        return level.getSignal(origin.offset(orientation.getPos(part)).relative(direction), direction);
    }

    protected int getDirectSignal(Level level, BlockPos pos, P part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(getPartProperty()) == part) {
            return level.getDirectSignal(pos.relative(direction), direction);
        }

        O orientation = state.getValue(getOrientationProperty());
        BlockPos origin = getOriginPositionFromState(state, pos);
        return level.getDirectSignal(origin.offset(orientation.getPos(part)).relative(direction), direction);
    }

    protected int getAnalogSignal(Level level, BlockPos pos, P part, Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(getPartProperty()) == part) {
            return level.getDirectSignal(pos.relative(direction), direction);
        }

        O orientation = state.getValue(getOrientationProperty());
        BlockPos origin = getOriginPositionFromState(state, pos);
        BlockPos targetLoc = origin.offset(orientation.getPos(part)).relative(direction);
        return level.getBlockState(targetLoc).getAnalogOutputSignal(level, targetLoc);
    }


    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(getOrientationProperty(), state.getValue(getOrientationProperty()).rotate(rot));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(getOrientationProperty(), state.getValue(getOrientationProperty()).mirror(mirror));
    }
}
