package net.kapitencraft.kap_lib.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2x2;
import net.minecraft.world.level.block.state.properties.Property;

public class Mp2x2BlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part2x2> {
    public Mp2x2BlockTest(Properties properties) {
        super(properties, Part2x2.TOP_LEFT);
    }

    @Override
    protected Property<HorizontalOrientation> getOrientationProperty() {
        return HorizontalOrientation.PROPERTY;
    }

    @Override
    protected Property<Part2x2> getPartProperty() {
        return Part2x2.PROPERTY;
    }

    @Override
    protected Part2x2[] getPartValues() {
        return Part2x2.values();
    }
}
