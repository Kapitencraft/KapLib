package net.kapitencraft.kap_lib.multiblock.structure.config;

import net.kapitencraft.kap_lib.multiblock.registry.ModBlocks;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;

public class MultiblockStructureConfigurationEditScreen extends Screen {
    private final MultiblockStructureConfigurationBlockEntity configuration;
    private EditBox sizeXEdit, sizeYEdit, sizeZEdit;
    
    protected MultiblockStructureConfigurationEditScreen(MultiblockStructureConfigurationBlockEntity configuration) {
        super(Component.translatable(ModBlocks.MULTIBLOCK_STRUCTURE_CONFIG.get().getDescriptionId()));
        this.configuration = configuration;
    }

    @Override
    protected void init() {
        super.init();
        Vec3i vec3i = this.configuration.getStructureSize();
        this.sizeXEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, Component.translatable("structure_block.size.x"));
        this.sizeXEdit.setMaxLength(15);
        this.sizeXEdit.setValue(Integer.toString(vec3i.getX()));
        this.addWidget(this.sizeXEdit);
        this.sizeYEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, Component.translatable("structure_block.size.y"));
        this.sizeYEdit.setMaxLength(15);
        this.sizeYEdit.setValue(Integer.toString(vec3i.getY()));
        this.addWidget(this.sizeYEdit);
        this.sizeZEdit = new EditBox(this.font, this.width / 2 + 8, 120, 80, 20, Component.translatable("structure_block.size.z"));
        this.sizeZEdit.setMaxLength(15);
        this.sizeZEdit.setValue(Integer.toString(vec3i.getZ()));
        this.addWidget(this.sizeZEdit);
    }
}
