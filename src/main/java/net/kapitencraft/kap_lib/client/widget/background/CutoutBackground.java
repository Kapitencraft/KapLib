package net.kapitencraft.kap_lib.client.widget.background;

import net.minecraft.client.gui.GuiGraphics;

public abstract class CutoutBackground extends WidgetBackground {

    @Override
    public final void render(boolean scissorEnabled, GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY) {
        if (!scissorEnabled) {
            graphics.enableScissor(x, y, x + width, y + height);
        }
        renderCutout(graphics, x, y, width, height, offsetX, offsetY);
        if (!scissorEnabled) {
            graphics.disableScissor();
        }
    }

    protected abstract void renderCutout(GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY);
}
