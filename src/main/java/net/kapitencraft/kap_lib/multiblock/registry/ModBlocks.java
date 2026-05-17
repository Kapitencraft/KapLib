package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfigurationBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ModBlocks {
    DeferredRegister<Block> REGISTRY = LibConstants.registry(Registries.BLOCK);

    Supplier<MultiblockStructureConfigurationBlock> MULTIBLOCK_STRUCTURE_CONFIG = REGISTRY.register("multiblock_structure_config", MultiblockStructureConfigurationBlock::new);
}
