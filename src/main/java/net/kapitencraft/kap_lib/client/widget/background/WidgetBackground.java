package net.kapitencraft.kap_lib.client.widget.background;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * simple implementation for backgrounds;
 * will scissor the screen if necessary
 */
public abstract class WidgetBackground {

    public abstract void render(boolean scissorEnabled, GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY);

    /**
     * @param texture the location pointing to the texture
     * @param texWidth the texture width in pixels
     * @param texHeight the texture height in pixels
     * @return a background that uses the texture
     */
    public static WidgetBackground texture(ResourceLocation texture, int texWidth, int texHeight) {
        return new TextureBackground(texture, texWidth, texHeight);
    }

    /**
     * @param color the color of the background
     * @return a background that fills it with one color
     */
    public static WidgetBackground fill(int color) {
        return new FillBackground(color);
    }
}
