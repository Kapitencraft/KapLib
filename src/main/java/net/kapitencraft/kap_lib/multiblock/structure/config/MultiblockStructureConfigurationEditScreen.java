package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.google.common.collect.ImmutableList;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlocks;
import net.kapitencraft.kap_lib.multiblock.structure.network.C2S.SetMultiblockStructureConfigurationBlockDataPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

public class MultiblockStructureConfigurationEditScreen extends Screen {
    private static final ImmutableList<MultiblockStructureConfigurationBlockEntity.Mode> ALL_MODES = ImmutableList.copyOf(MultiblockStructureConfigurationBlockEntity.Mode.values());
    private static final ImmutableList<MultiblockStructureConfigurationBlockEntity.Mode> DEFAULT_MODES = ALL_MODES.stream()
            .filter(p_169859_ -> p_169859_ != MultiblockStructureConfigurationBlockEntity.Mode.SAVE)
            .collect(ImmutableList.toImmutableList());

    private MultiblockStructureConfigurationBlockEntity.Mode initialMode = MultiblockStructureConfigurationBlockEntity.Mode.SAVE;

    private final MultiblockStructureConfigurationBlockEntity configuration;
    private EditBox sizeXEdit, sizeYEdit, sizeZEdit;
    private EditBox nameEdit;
    
    protected MultiblockStructureConfigurationEditScreen(MultiblockStructureConfigurationBlockEntity configuration) {
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

        this.addRenderableWidget(
                CycleButton.<MultiblockStructureConfigurationBlockEntity.Mode>builder(p_169852_ -> Component.translatable("structure_block.mode." + p_169852_.getSerializedName()))
                        .withValues(DEFAULT_MODES, ALL_MODES)
                        .displayOnlyValue()
                        .withInitialValue(this.initialMode)
                        .create(this.width / 2 - 4 - 150, 185, 50, 20, Component.literal("MODE"), (p_169846_, mode) -> {
                            this.configuration.setMode(mode);
                            this.updateMode(mode);
                        })
        );
    }

    private void updateMode(MultiblockStructureConfigurationBlockEntity.Mode structureMode) {
        this.nameEdit.setVisible(false);
        this.sizeXEdit.setVisible(false);
        this.sizeYEdit.setVisible(false);
        this.sizeZEdit.setVisible(false);
        switch (structureMode) {
            case SAVE:
                this.sizeXEdit.setVisible(true);
                this.sizeYEdit.setVisible(true);
                this.sizeZEdit.setVisible(true);
                break;
            case CORNER:
                this.nameEdit.setVisible(true);
                break;
        }
    }

    private boolean sendToServer(MultiblockStructureConfigurationBlockEntity.UpdateType updateType) {
        Vec3i vec3i = new Vec3i(
                this.parseCoordinate(this.sizeXEdit.getValue()), this.parseCoordinate(this.sizeYEdit.getValue()), this.parseCoordinate(this.sizeZEdit.getValue())
        );
        this.minecraft
                .getConnection()
                .send(
                        new SetMultiblockStructureConfigurationBlockDataPacket(
                                this.configuration.getBlockPos(),
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
