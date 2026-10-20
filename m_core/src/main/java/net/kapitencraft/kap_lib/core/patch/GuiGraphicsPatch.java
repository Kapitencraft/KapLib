package net.kapitencraft.kap_lib.core.patch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface GuiGraphicsPatch {
    /**
     * similar to {@link GuiGraphics#fill(int, int, int, int, int) GuiGraphics#fill}
     * <br>but uses floats as positioning
     */
    default void fill(float xStart, float yStart, float xEnd, float yEnd, int color, int blitOffset) {
        throw new AssertionError();
    }

    /**
     * draws the given text in the center of the given area
     * @param toDraw the text
     * @param color the color
     */
    default void drawCenteredString(int xStart, int yStart, int xEnd, int yEnd, Component toDraw, int color) {
        Font font = Minecraft.getInstance().font;
        int height = yEnd - yStart;
        int width = xEnd - xStart;
        int xDrawStart = xStart + width / 2;
        int yDrawStart = yStart + height / 2 - 4;
        ((GuiGraphics) this).drawCenteredString(font, toDraw, xDrawStart, yDrawStart, color);
    }
}
