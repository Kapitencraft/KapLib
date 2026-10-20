package net.kapitencraft.kap_lib.core.mixin.classes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kapitencraft.kap_lib.core.patch.GuiGraphicsPatch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements GuiGraphicsPatch {

    @Shadow
    @Final
    private PoseStack pose;

    @Override
    public void fill(float xStart, float yStart, float xEnd, float yEnd, int color, int blitOffset) {
        innerFill(pose.last().pose(), xStart, yStart, xEnd, yEnd, color, blitOffset);
    }

    @Unique
    private static void innerFill(Matrix4f p_254518_, float xStart, float yStart, float xEnd, float yEnd, int color, int blitOffset) {
        if (xStart < xEnd) {
            float i = xStart;
            xStart = xEnd;
            xEnd = i;
        }

        if (yStart < yEnd) {
            float i = yStart;
            yStart = yEnd;
            yEnd = i;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.addVertex(p_254518_, xStart, yEnd, blitOffset).setColor(f, f1, f2, f3);
        builder.addVertex(p_254518_, xEnd, yEnd, blitOffset).setColor(f, f1, f2, f3);
        builder.addVertex(p_254518_, xEnd, yStart, blitOffset).setColor(f, f1, f2, f3);
        builder.addVertex(p_254518_, xStart, yStart, blitOffset).setColor(f, f1, f2, f3);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
    }
}
