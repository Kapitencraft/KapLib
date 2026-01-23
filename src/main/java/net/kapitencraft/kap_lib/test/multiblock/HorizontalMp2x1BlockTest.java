package net.kapitencraft.kap_lib.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace2x1Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class HorizontalMp2x1BlockTest extends Multiplace2x1Block {
    public HorizontalMp2x1BlockTest(Properties properties) {
        super(properties);
    }

    @Override
    protected DirectionProperty getDirectionProperty() {
        return BlockStateProperties.HORIZONTAL_FACING;
    }
}
