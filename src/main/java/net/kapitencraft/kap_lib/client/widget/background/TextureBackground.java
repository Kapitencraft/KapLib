package net.kapitencraft.kap_lib.client.widget.background;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Objects;

/**
 * widget background for a texture
 */
public class TextureBackground extends CutoutBackground {
    private final ResourceLocation texture;
    private final int textureWidth, textureHeight;

    TextureBackground(ResourceLocation texture, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderCutout(GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY) {
        graphics.pose().pushPose();
        graphics.pose().translate((float)x, (float)y, 0.0F);
        ResourceLocation resourcelocation = Objects.requireNonNullElse(this.texture, TextureManager.INTENTIONAL_MISSING_TEXTURE);
        int xBackGround = Mth.floor(offsetX);
        int yBackGround = Mth.floor(offsetY);
        int backgroundXStart = xBackGround % textureWidth;
        int backgroundYStart = yBackGround % textureHeight;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 8; ++j1) {
                graphics.blit(resourcelocation, backgroundXStart + textureWidth * i1, backgroundYStart + textureHeight * j1, 0.0F, 0.0F, textureWidth, textureHeight, textureWidth, textureHeight);
            }
        }
        graphics.pose().popPose();
    }
}
