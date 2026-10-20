package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2x3;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class Mp2x3BlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part2x3> {
    public Mp2x3BlockTest(Properties properties) {
        super(properties, Part2x3.TOP_LEFT);
    }

    @Override
    protected HorizontalOrientation getOrientation(BlockState state) {
        return HorizontalOrientation.SOUTH_EAST;
    }

    @Override
    protected Property<Part2x3> getPartProperty() {
        return Part2x3.PROPERTY;
    }

    @Override
    protected HorizontalOrientation determineOrientation(@NotNull BlockPlaceContext context) {
        return null;
    }

    @Override
    protected Part2x3[] getPartValues() {
        return Part2x3.values();
    }
}
