package net.kapitencraft.kap_lib.client.widget.background.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kapitencraft.kap_lib.client.widget.background.CutoutBackground;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Optional;

/**
 * widget background for a texture
 */
public class TextureBackground extends CutoutBackground {
    private final TextureAtlasSprite texture;
    private final int textureWidth, textureHeight;

    public TextureBackground(TextureAtlasSprite texture, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderCutout(GuiGraphics graphics, int x, int y, int width, int height, float offsetX, float offsetY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate((float)x, (float)y, 0.0F);
        int xBackGround = Mth.floor(offsetX);
        int yBackGround = Mth.floor(offsetY);
        int backgroundXStart = xBackGround % textureWidth;
        int backgroundYStart = yBackGround % textureHeight;

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = pose.last().pose();
        RenderSystem.setShaderTexture(0, texture.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Optional<BackgroundTileableSection> section = texture.contents().metadata().getSection(BackgroundTileableSection.SERIALIZER);
        boolean render2x2 = section.isPresent() && section.get().type() == BackgroundTileableSection.TileType.TWO_BY_TWO;

        if (render2x2) {
            for (int i1 = -1; i1 <= width / textureWidth / 2; ++i1) {
                for (int j1 = -1; j1 <= height / textureHeight / 2; ++j1) {
                    int xStart = backgroundXStart + textureWidth * i1 * 2;
                    int yStart = backgroundYStart + textureHeight * j1 * 2;
                    render2x2Element0(builder, matrix4f, xStart, yStart);
                }
            }
        } else {
            for (int i1 = -1; i1 <= width / textureWidth; ++i1) {
                for (int j1 = -1; j1 <= height / textureHeight; ++j1) {
                    int xStart = backgroundXStart + textureWidth * i1;
                    int yStart = backgroundYStart + textureHeight * j1;

                    builder.addVertex(matrix4f, xStart, yStart, 0).setUv(texture.getU0(), texture.getV0());
                    builder.addVertex(matrix4f, xStart, yStart + textureHeight, 0).setUv(texture.getU0(), texture.getV1());
                    builder.addVertex(matrix4f, xStart + textureWidth, yStart + textureHeight, 0).setUv(texture.getU1(), texture.getV1());
                    builder.addVertex(matrix4f, xStart + textureWidth, yStart, 0).setUv(texture.getU1(), texture.getV0());
                }
            }
        }


        BufferUploader.drawWithShader(builder.buildOrThrow());
        pose.popPose();
    }

    private void render2x2Element0(BufferBuilder builder, Matrix4f matrix4f, int xStart, int yStart) {
        int xMiddle = xStart + textureWidth;
        int yMiddle = yStart + textureHeight;
        int xEnd = xMiddle + textureWidth;
        int yEnd = yMiddle + textureHeight;

        builder.addVertex(matrix4f, xStart, yStart, 0).setUv(texture.getU0(), texture.getV0());
        builder.addVertex(matrix4f, xStart, yMiddle, 0).setUv(texture.getU0(), texture.getV1());
        builder.addVertex(matrix4f, xMiddle, yMiddle, 0).setUv(texture.getU1(), texture.getV1());
        builder.addVertex(matrix4f, xMiddle, yStart, 0).setUv(texture.getU1(), texture.getV0());

        builder.addVertex(matrix4f, xMiddle, yStart, 0).setUv(texture.getU0(), texture.getV1());
        builder.addVertex(matrix4f, xMiddle, yMiddle, 0).setUv(texture.getU1(), texture.getV1());
        builder.addVertex(matrix4f, xEnd, yMiddle, 0).setUv(texture.getU1(), texture.getV0());
        builder.addVertex(matrix4f, xEnd, yStart, 0).setUv(texture.getU0(), texture.getV0());

        builder.addVertex(matrix4f, xStart, yMiddle, 0).setUv(texture.getU1(), texture.getV0());
        builder.addVertex(matrix4f, xStart, yEnd, 0).setUv(texture.getU0(), texture.getV0());
        builder.addVertex(matrix4f, xMiddle, yEnd, 0).setUv(texture.getU0(), texture.getV1());
        builder.addVertex(matrix4f, xMiddle, yMiddle, 0).setUv(texture.getU1(), texture.getV1());

        builder.addVertex(matrix4f, xMiddle, yMiddle, 0).setUv(texture.getU1(), texture.getV1());
        builder.addVertex(matrix4f, xMiddle, yEnd, 0).setUv(texture.getU1(), texture.getV0());
        builder.addVertex(matrix4f, xEnd, yEnd, 0).setUv(texture.getU0(), texture.getV0());
        builder.addVertex(matrix4f, xEnd, yMiddle, 0).setUv(texture.getU0(), texture.getV1());
    }
}
