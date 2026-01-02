package net.kapitencraft.kap_lib.core.client.gui.screen;

import net.kapitencraft.kap_lib.core.client.widget.text.MultiLineTextBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * used to test lib code, do not use!
 */
@ApiStatus.Internal
public class TestScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("textures/block/diamond_block.png");
    private MultiLineTextBox textBox;

    public TestScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        textBox = new MultiLineTextBox(this.font, 10, 10, width - 20, height - 20, textBox, Component.empty());
        textBox.setTextureBackground(ResourceLocation.withDefaultNamespace("block/orange_glazed_terracotta"));
        textBox.setLineRenderType(MultiLineTextBox.LineRenderType.EVERY);
        this.addRenderableWidget(textBox);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        this.textBox.tick();
    }

    @Override
    public void onClose() {
        this.textBox.onClose();
        super.onClose();
    }
}
