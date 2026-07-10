package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.LargeBEMultiplaceBlock;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.HorizontalOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.Part2Cubed;
import net.kapitencraft.kap_lib.multiblock.test.multiblock.entity.TestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class Mp2CubedBlockTest extends LargeBEMultiplaceBlock<HorizontalOrientation, Part2Cubed, TestBlockEntity> {
    public Mp2CubedBlockTest(Properties properties) {
        super(properties, Part2Cubed.FRONT_BOTTOM_LEFT);
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

    @Override
    protected TestBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }
}
