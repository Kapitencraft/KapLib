package net.kapitencraft.kap_lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface UsefulTextures {
    ResourceLocation CHECK_MARK = getGuiLocation("checkmark.png");
    ResourceLocation CROSS = KapLibMod.res("textures/gui/red_cross.png");
    ResourceLocation SLIDER = getGuiLocation("container/loom.png");
    ResourceLocation ARROWS = getGuiLocation("server_selection.png");
    private static ResourceLocation getGuiLocation(String path) {
        return new ResourceLocation("textures/gui/" + path);
    }

    static void renderCheckMark(GuiGraphics graphics, int checkBoxX, int checkBoxY) {
        graphics.blit(CHECK_MARK, checkBoxX, checkBoxY, 0, 0, 0, 8, 8, 8, 8);
    }

    static void renderCross(GuiGraphics graphics, int x, int y, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(size / 8f, size / 7f, 0);
        graphics.blit(CROSS, 0, 0, 0, 0, 0, 8, 7, 8, 7);
        graphics.pose().popPose();
    }

    static void renderSliderWithLine(GuiGraphics graphics, int sliderWidth, boolean showLine, float movePercent, int rightAlignment, int minY, int height) {
        graphics.fill(rightAlignment - sliderWidth, minY, rightAlignment, minY + height, 0x2DFFFFFF);
        UsefulTextures.renderSlider(graphics, rightAlignment - sliderWidth, minY + (int) (movePercent * (height - sliderHeightForWidth(sliderWidth))), showLine, sliderWidth / 12f);
    }

    static int sliderHeightForWidth(int width) {
        return 15 * width / 12;
    }

    static void renderSlider(GuiGraphics graphics, int x, int y, boolean light, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 0);
        graphics.blit(SLIDER, 0, 0, 232 + (light ? 0 : 12), 0, 12, 15);
        graphics.pose().popPose();
    }

    static void renderUpButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop, 0);
        float scale = size / 16f;
        graphics.pose().scale(scale, scale, 0);
        if (hovered) {
            graphics.blit(ARROWS, 0, 0, 96, 32, 32, 32);
        } else {
            graphics.blit(ARROWS, 0, 0, 96, 0, 32, 32);
        }
        graphics.pose().popPose();
    }

    static void renderDownButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop - size, 0);
        graphics.pose().scale(size / 16f, size / 16f, 0);
        RenderSystem.setShaderTexture(0, ARROWS);
        if (hovered) {
            graphics.blit(ARROWS, 0, 0, 64, 32, 32, 32);
        } else {
            graphics.blit(ARROWS, 0, 0, 64, 0, 32, 32);
        }
        graphics.pose().popPose();
    }

    static void renderCheckBox(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active) {
        graphics.fill(x - 1, y - 1, x + 9, y + 9, backgroundColor);
        if (active) renderCheckMark(graphics, x, y);
    }

    static void renderCheckBoxWithText(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active, Font font, int textColor, Component text) {
        renderCheckBox(graphics, x, y, backgroundColor, active);
        graphics.drawString(font, text, x + 11, y, textColor);
    }

    static void renderCheckBoxWithText(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active, Font font, int textColor, Component text, boolean dropShadow) {
        renderCheckBox(graphics, x, y, backgroundColor, active);
        graphics.drawString(font, text, x + 11, y, textColor, dropShadow);
    }
}