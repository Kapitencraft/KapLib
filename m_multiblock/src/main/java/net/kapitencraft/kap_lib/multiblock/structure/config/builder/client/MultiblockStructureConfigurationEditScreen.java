package net.kapitencraft.kap_lib.multiblock.structure.config.builder.client;

import com.google.common.collect.ImmutableList;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlocks;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfigurationBlockEntity;
import net.kapitencraft.kap_lib.multiblock.structure.network.C2S.SetMultiblockStructureConfigurationBlockDataPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MultiblockStructureConfigurationEditScreen extends Screen {
    private static final ImmutableList<MultiblockStructureConfigurationBlockEntity.Mode> ALL_MODES = ImmutableList.copyOf(MultiblockStructureConfigurationBlockEntity.Mode.values());

    private MultiblockStructureConfigurationBlockEntity.Mode initialMode = MultiblockStructureConfigurationBlockEntity.Mode.SAVE;

    private final MultiblockStructureConfigurationBlockEntity configuration;
    private ConfigureGroupsWidget groupSelector;
    private EditBox nameEdit;
    private EditBox posXEdit;
    private EditBox posYEdit;
    private EditBox posZEdit;
    private EditBox sizeXEdit, sizeYEdit, sizeZEdit;
    private Button saveButton;
    private Button detectButton;
    
    public MultiblockStructureConfigurationEditScreen(MultiblockStructureConfigurationBlockEntity configuration) {
        super(Component.translatable(MBBlocks.MULTIBLOCK_STRUCTURE_CONFIG.get().getDescriptionId()));
        this.configuration = configuration;
    }

    private void onDone() {
        if (this.sendToServer(MultiblockStructureConfigurationBlockEntity.UpdateType.UPDATE_DATA)) {
            this.minecraft.setScreen(null);
        }
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, p_99460_ -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, p_99457_ -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());
        this.groupSelector = new ConfigureGroupsWidget(
                this.width / 2 - 320, 40, 150, 300, Component.translatable("cmsb.groups")
        );
        this.groupSelector.importFrom(this.configuration);
        this.addRenderableWidget(this.groupSelector);
        BlockPos blockpos = this.configuration.getStructurePos();
        this.posXEdit = new EditBox(this.font, this.width / 2 - 152, 80, 80, 20, Component.translatable("structure_block.position.x"));
        this.posXEdit.setMaxLength(15);
        this.posXEdit.setValue(Integer.toString(blockpos.getX()));
        this.addRenderableWidget(this.posXEdit);
        this.posYEdit = new EditBox(this.font, this.width / 2 - 72, 80, 80, 20, Component.translatable("structure_block.position.y"));
        this.posYEdit.setMaxLength(15);
        this.posYEdit.setValue(Integer.toString(blockpos.getY()));
        this.addRenderableWidget(this.posYEdit);
        this.posZEdit = new EditBox(this.font, this.width / 2 + 8, 80, 80, 20, Component.translatable("structure_block.position.z"));
        this.posZEdit.setMaxLength(15);
        this.posZEdit.setValue(Integer.toString(blockpos.getZ()));
        this.addRenderableWidget(this.posZEdit);
        Vec3i vec3i = this.configuration.getStructureSize();
        this.sizeXEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, Component.translatable("structure_block.size.x"));
        this.sizeXEdit.setMaxLength(15);
        this.sizeXEdit.setValue(Integer.toString(vec3i.getX()));
        this.addRenderableWidget(this.sizeXEdit);
        this.sizeYEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, Component.translatable("structure_block.size.y"));
        this.sizeYEdit.setMaxLength(15);
        this.sizeYEdit.setValue(Integer.toString(vec3i.getY()));
        this.addRenderableWidget(this.sizeYEdit);
        this.sizeZEdit = new EditBox(this.font, this.width / 2 + 8, 120, 80, 20, Component.translatable("structure_block.size.z"));
        this.sizeZEdit.setMaxLength(15);
        this.sizeZEdit.setValue(Integer.toString(vec3i.getZ()));
        this.addRenderableWidget(this.sizeZEdit);
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, Component.translatable("structure_block.structure_name")) {
            @Override
            public boolean charTyped(char p_99476_, int p_99477_) {
                return MultiblockStructureConfigurationEditScreen.this.isValidCharacterForName(this.getValue(), p_99476_, this.getCursorPosition()) && super.charTyped(p_99476_, p_99477_);
            }
        };
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(this.configuration.getStructureName());
        this.addRenderableWidget(this.nameEdit);

        this.initialMode = configuration.getMode();
        this.addRenderableWidget(
                CycleButton.<MultiblockStructureConfigurationBlockEntity.Mode>builder(p_169852_ -> Component.translatable("structure_block.mode." + p_169852_.getSerializedName()))
                        .withValues(ALL_MODES)
                        .displayOnlyValue()
                        .withInitialValue(this.initialMode)
                        .create(this.width / 2 - 4 - 150, 185, 50, 20, Component.literal("MODE"), (p_169846_, mode) -> {
                            this.configuration.setMode(mode);
                            this.updateMode(mode);
                        })
        );
        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("structure_block.button.save"), p_280866_ -> {
            if (this.configuration.getMode() == MultiblockStructureConfigurationBlockEntity.Mode.SAVE) {
                this.sendToServer(MultiblockStructureConfigurationBlockEntity.UpdateType.SAVE_CONFIGURATION);
                this.minecraft.setScreen(null);
            }
        }).bounds(this.width / 2 + 4 + 100, 185, 50, 20).build());
        this.detectButton = this.addRenderableWidget(Button.builder(Component.translatable("structure_block.button.detect_size"), p_280865_ -> {
            if (this.configuration.getMode() == MultiblockStructureConfigurationBlockEntity.Mode.SAVE) {
                this.sendToServer(MultiblockStructureConfigurationBlockEntity.UpdateType.SCAN_AREA);
                this.minecraft.setScreen(null);
            }
        }).bounds(this.width / 2 + 4 + 100, 120, 50, 20).build());

        this.updateMode(this.initialMode);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    private void updateMode(MultiblockStructureConfigurationBlockEntity.Mode structureMode) {
        this.sizeXEdit.setVisible(false);
        this.sizeYEdit.setVisible(false);
        this.sizeZEdit.setVisible(false);
        this.posXEdit.setVisible(false);
        this.posYEdit.setVisible(false);
        this.posZEdit.setVisible(false);
        this.groupSelector.visible = false;
        this.saveButton.visible = false;
        this.detectButton.visible = false;
        switch (structureMode) {
            case SAVE:
                this.sizeXEdit.setVisible(true);
                this.sizeYEdit.setVisible(true);
                this.sizeZEdit.setVisible(true);
                this.posXEdit.setVisible(true);
                this.posYEdit.setVisible(true);
                this.posZEdit.setVisible(true);
                this.groupSelector.visible = true;
                this.saveButton.visible = true;
                this.detectButton.visible = true;
                break;
            case CORNER:
                break;
        }
    }

    private boolean sendToServer(MultiblockStructureConfigurationBlockEntity.UpdateType updateType) {
        BlockPos blockpos = new BlockPos(
                this.parseCoordinate(this.posXEdit.getValue()), this.parseCoordinate(this.posYEdit.getValue()), this.parseCoordinate(this.posZEdit.getValue())
        );
        Vec3i vec3i = new Vec3i(
                this.parseCoordinate(this.sizeXEdit.getValue()), this.parseCoordinate(this.sizeYEdit.getValue()), this.parseCoordinate(this.sizeZEdit.getValue())
        );
        this.minecraft
                .getConnection()
                .send(
                        new SetMultiblockStructureConfigurationBlockDataPacket(
                                this.configuration.getMode(),
                                this.configuration.getBlockPos(),
                                blockpos,
                                vec3i,
                                this.nameEdit.getValue(),
                                updateType
                        )
                );
        return true;
    }

    private int parseCoordinate(String coordinate) {
        try {
            return Integer.parseInt(coordinate);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }
}
