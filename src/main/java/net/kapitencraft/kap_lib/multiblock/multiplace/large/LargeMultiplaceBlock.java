package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.MultiblockOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.Orientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public abstract class LargeMultiplaceBlock<O extends MultiblockOrientation<O>, P extends MultiplaceBlockPart<P>> extends Block {
    //DOWN -> NORTH, WEST
    //UP   -> SOUTH, EAST
    //NORTH-> WEST, DOWN
    //SOUTH->

    private final P origin;

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
    protected abstract Property<P> getPartProperty();

    @Override
    protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        O orientation = state.getValue(getOrientationProperty());

        Property<P> partProperty = getPartProperty();
        if (isNeighbourDirection(state.getValue(partProperty), orientation, facing)) {
            return facingState.is(this) && facingState.getValue(partProperty) != state.getValue(partProperty)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
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

    private boolean isNeighbourDirection(P part, O orientation, Direction direction) {
        BlockPos pos = orientation.getPos(part); //relative position to origin
        int axis = pos.get(direction.getAxis());
        int step = direction.getAxisDirection().getStep();
        return axis * -step > 0;
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

    protected BlockPos getOriginPositionFromState(BlockState state, BlockPos pos) {
        return getOriginPosition(state.getValue(getOrientationProperty()), state.getValue(getPartProperty()), pos);
    }

    protected boolean isOrigin(BlockState state) {
        return state.getValue(getPartProperty()) == origin;
    }
}
