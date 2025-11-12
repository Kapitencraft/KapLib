package net.kapitencraft.kap_lib.client.widget.background;

import net.kapitencraft.kap_lib.client.widget.background.texture.TextureBackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;

/**
 * simple implementation for backgrounds;
 * will scissor the screen if necessary
 */
public abstract class WidgetBackground {

    public abstract void render(boolean scissorEnabled, GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY);

    /**
     * @param atlas the location of the atlas to use
     * @param atlasLocation the location inside the used atlas
     * @param texWidth the texture width in pixels
     * @param texHeight the texture height in pixels
     * @return a background that uses the texture
     */
    public static WidgetBackground texture(ResourceLocation atlas, ResourceLocation atlasLocation, int texWidth, int texHeight) {
        return new TextureBackground(Minecraft.getInstance().getTextureAtlas(atlas).apply(atlasLocation), texWidth, texHeight);
    }

    /**
     * @param color the color of the background
     * @return a background that fills it with one color
     */
    public static WidgetBackground fill(int color) {
        return new FillBackground(color);
    }
}
