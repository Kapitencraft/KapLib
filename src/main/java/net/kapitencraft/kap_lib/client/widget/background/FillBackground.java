package net.kapitencraft.kap_lib.client.widget.background;

import net.minecraft.client.gui.GuiGraphics;

/**
 * background to fill with color
 */
public class FillBackground extends WidgetBackground {
    private final int color;

    public FillBackground(int color) {
        this.color = color;
    }

    @Override
    public void render(boolean scissorEnabled, GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY) {
        graphics.fill(x, y, x + width, y + height, color);
    }
}
