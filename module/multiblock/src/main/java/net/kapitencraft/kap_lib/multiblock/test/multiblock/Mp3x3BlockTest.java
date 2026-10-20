package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.MultiplaceLargeDirectionalBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part3x3;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class Mp3x3BlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part3x3> implements MultiplaceLargeDirectionalBlock<HorizontalOrientation> {
    private static final Property<HorizontalOrientation> ORIENTATION = HorizontalOrientation.PROPERTY;

    public Mp3x3BlockTest(Properties properties) {
        super(properties, Part3x3.TOP_LEFT);
    }

    @Override
    public Property<HorizontalOrientation> getOrientationProperty() {
        return ORIENTATION;
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return MultiplaceLargeDirectionalBlock.super.rotate(state, rot);
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return MultiplaceLargeDirectionalBlock.super.mirror(state, mirror);
    }

    @Override
    protected HorizontalOrientation getOrientation(BlockState state) {
        return state.getValue(ORIENTATION);
    }

    @Override
    protected Property<Part3x3> getPartProperty() {
        return Part3x3.PROPERTY;
    }

    @Override
    protected HorizontalOrientation determineOrientation(@NotNull BlockPlaceContext context) {
        Direction horizontal = context.getHorizontalDirection();
        return HorizontalOrientation.from(horizontal, horizontal.getClockWise());
    }

    @Override
    protected Part3x3[] getPartValues() {
        return Part3x3.values();
    }
}
