package net.kapitencraft.kap_lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.gui.GuiGraphics;
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

    static void renderSlider(GuiGraphics graphics, int x, int y, boolean light, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 0);
        graphics.blit(SLIDER, 0, 0, 232 + (light ? 0 : 12), 0, 12, 15, 256, 256);
        graphics.pose().popPose();
    }

    static void renderUpButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop, 0);
        float scale = size / 16f;
        graphics.pose().scale(scale, scale, 0);
        if (hovered) {
            graphics.blit(ARROWS, 0, 0, 96.0F, 32.0F, 32, 32, 256, 256);
        } else {
            graphics.blit(ARROWS, 0, 0, 96.0F, 0.0F, 32, 32, 256, 256);
        }
        graphics.pose().popPose();
    }

    static void renderDownButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop - size, 0);
        graphics.pose().scale(size / 16f, size / 16f, 0);
        RenderSystem.setShaderTexture(0, ARROWS);
        if (hovered) {
            graphics.blit(ARROWS, 0, 0, 64.0F, 32.0F, 32, 32, 256, 256);
        } else {
            graphics.blit(ARROWS, 0, 0, 64.0F, 0.0F, 32, 32, 256, 256);
        }
        graphics.pose().popPose();
    }
}