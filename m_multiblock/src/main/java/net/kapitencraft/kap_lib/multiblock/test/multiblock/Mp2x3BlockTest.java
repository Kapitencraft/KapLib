package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2x3;
import net.minecraft.world.level.block.state.properties.Property;

public class Mp2x3BlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part2x3> {
    public Mp2x3BlockTest(Properties properties) {
        super(properties, Part2x3.TOP_LEFT);
    }

    @Override
    protected Property<HorizontalOrientation> getOrientationProperty() {
        return HorizontalOrientation.PROPERTY;
    }

    @Override
    protected Property<Part2x3> getPartProperty() {
        return Part2x3.PROPERTY;
    }

    @Override
    protected Part2x3[] getPartValues() {
        return Part2x3.values();
    }
}
