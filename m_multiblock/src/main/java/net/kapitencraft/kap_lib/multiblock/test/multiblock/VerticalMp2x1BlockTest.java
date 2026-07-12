package net.kapitencraft.kap_lib.multiblock.test.multiblock;

import net.kapitencraft.kap_lib.multiblock.multiplace.line.Multiplace2x1Block;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public class VerticalMp2x1BlockTest extends Multiplace2x1Block {
    public VerticalMp2x1BlockTest(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull Direction getDirection(BlockState state) {
        return Direction.UP;
    }

    @Override
    protected Direction determinPlaceDirection(BlockPlaceContext context) {
        return Direction.UP;
    }
}
