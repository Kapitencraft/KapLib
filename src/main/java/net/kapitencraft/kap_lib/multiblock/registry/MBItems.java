package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfiguratorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface MBItems {
    DeferredRegister.Items REGISTRY = DeferredRegister.createItems(LibConstants.MOD_ID);

    DeferredItem<MultiblockStructureConfiguratorItem> STRUCTURE_CONFIGURATOR = REGISTRY.registerItem("multiblock_structure_configurator", MultiblockStructureConfiguratorItem::new);
}