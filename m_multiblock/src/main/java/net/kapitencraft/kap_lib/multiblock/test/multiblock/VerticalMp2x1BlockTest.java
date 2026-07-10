package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace2x1Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class VerticalMp2x1BlockTest extends Multiplace2x1Block {
    public VerticalMp2x1BlockTest(Properties properties) {
        super(properties);
    }

    @Override
    protected DirectionProperty getDirectionProperty() {
        return BlockStateProperties.VERTICAL_DIRECTION;
    }
}
