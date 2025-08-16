package net.kapitencraft.kap_lib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.client.widget.text.MultiLineTextBox;
import net.kapitencraft.kap_lib.client.widget.background.WidgetBackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
        final int widgetWidth = 400, widgetHeight = 200;
        textBox = new MultiLineTextBox(this.font, width / 2 - widgetWidth / 2, height / 2 - widgetHeight / 2, widgetWidth, widgetHeight, textBox, Component.empty());
        textBox.setBackground(WidgetBackground.fill(0xFF000000));
        textBox.setLineRenderType(MultiLineTextBox.LineRenderType.EVERY);
        //this.addRenderableWidget(textBox);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(0, 0, width, height, 0x408F8F8F);
        pGuiGraphics.blit(PlayerHeadAllocator.FONT, 0, 0, 0, 0, 360, 360, 360, 360);
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
