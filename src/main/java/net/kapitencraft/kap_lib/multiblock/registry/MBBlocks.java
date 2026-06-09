package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfigurationBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MBBlocks {
    DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(LibConstants.MOD_ID);

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, T> constructor) {
        DeferredBlock<T> block = REGISTRY.registerBlock(name, constructor);
        MBItems.REGISTRY.registerSimpleBlockItem(block);
        return block;
    }

    Supplier<MultiblockStructureConfigurationBlock> MULTIBLOCK_STRUCTURE_CONFIG = registerBlockWithItem("multiblock_structure_config", MultiblockStructureConfigurationBlock::new);
}
