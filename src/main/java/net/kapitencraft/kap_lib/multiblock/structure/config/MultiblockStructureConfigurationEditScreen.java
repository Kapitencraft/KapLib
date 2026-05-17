package net.kapitencraft.kap_lib.multiblock.structure.config;

import net.kapitencraft.kap_lib.multiblock.registry.ModBlocks;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MultiblockStructureConfigurationEditScreen extends Screen {
    private EditBox sizeXEdit, sizeYEdit, sizeZEdit;
    
    protected MultiblockStructureConfigurationEditScreen() {
        super(Component.translatable(ModBlocks.MULTIBLOCK_STRUCTURE_CONFIG.get().getDescriptionId()));
    }

    @Override
    protected void init() {
        super.init();
    }
}
