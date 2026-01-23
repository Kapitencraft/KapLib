package net.kapitencraft.kap_lib.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2Cubed;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2x3;
import net.minecraft.world.level.block.state.properties.Property;

public class Mp2CubedBlockTest extends LargeMultiplaceBlock<HorizontalOrientation, Part2Cubed> {
    public Mp2CubedBlockTest(Properties properties) {
        super(properties, Part2Cubed.FRONT_TOP_LEFT);
    }

    @Override
    protected Property<HorizontalOrientation> getOrientationProperty() {
        return HorizontalOrientation.PROPERTY;
    }

    @Override
    protected Property<Part2Cubed> getPartProperty() {
        return Part2Cubed.PROPERTY;
    }

    @Override
    protected Part2Cubed[] getPartValues() {
        return Part2Cubed.values();
    }
}
