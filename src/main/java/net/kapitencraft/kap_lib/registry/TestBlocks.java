package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.test.multiblock.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public interface TestBlocks {
    DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks("test");

    DeferredBlock<HorizontalMp2x1BlockTest> HORIZONTAL_MP_2X1_TEST = registerBlock("h_mp_2x1_test", HorizontalMp2x1BlockTest::new);
    DeferredBlock<Mp2x1BlockTest> MP_2X1_TEST = registerBlock("mp_2x1_test", Mp2x1BlockTest::new);
    DeferredBlock<VerticalMp2x1BlockTest> VERTICAL_MP_2X1_TEST = registerBlock("vertical_mp_2x1_test", VerticalMp2x1BlockTest::new);
    DeferredBlock<Mp3x1TestBlock> MP_3X1_TEST = registerBlock("mp_3x1_test", Mp3x1TestBlock::new);
    DeferredBlock<Mp2x2BlockTest> MP_2X2_TEST = registerBlock("mp_2x2_test", Mp2x2BlockTest::new);
    DeferredBlock<Mp2x3BlockTest> MP_2X3_TEST = registerBlock("mp_2x3_test", Mp2x3BlockTest::new);
    DeferredBlock<Mp3x3BlockTest> MP_3X3_TEST = registerBlock("mp_3x3_test", Mp3x3BlockTest::new);
    DeferredBlock<Mp2CubedBlockTest> MP_2_CUBED_TEST = registerBlock("mp_2_cubed_test", Mp2CubedBlockTest::new);

    private static <T extends Block, K extends BlockItem> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block) {
        DeferredBlock<T> toReturn = REGISTRY.registerBlock(name, block);
        registerItem(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    private static <T extends Block, K extends BlockItem> DeferredItem<K> registerItem(String name, Supplier<K> sup) {
        return TestItems.REGISTRY.register(name, sup);
    }

}
