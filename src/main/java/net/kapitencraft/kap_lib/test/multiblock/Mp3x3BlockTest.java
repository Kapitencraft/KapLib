package net.kapitencraft.kap_lib.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part3x3;
import net.minecraft.world.level.block.state.properties.Property;

public class Mp3x3BlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part3x3> {
    public Mp3x3BlockTest(Properties properties) {
        super(properties, Part3x3.TOP_LEFT);
    }

    @Override
    protected Property<HorizontalOrientation> getOrientationProperty() {
        return HorizontalOrientation.PROPERTY;
    }

    @Override
    protected Property<Part3x3> getPartProperty() {
        return Part3x3.PROPERTY;
    }

    @Override
    protected Part3x3[] getPartValues() {
        return Part3x3.values();
    }
}
