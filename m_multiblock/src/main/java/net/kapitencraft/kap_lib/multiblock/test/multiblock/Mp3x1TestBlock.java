package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace3x1Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class Mp3x1TestBlock extends Multiplace3x1Block {
    public Mp3x1TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected DirectionProperty getDirectionProperty() {
        return BlockStateProperties.FACING;
    }
}
