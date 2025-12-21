package net.kapitencraft.kap_lib.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerPatternRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final ModelPart FLAG;


    static {
        FLAG = MINECRAFT.getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
        FLAG.xRot = 0.0F;
        FLAG.y = -32.0F;
    }

    /**
     * @return the flag used by the renderer; do <i>not</i> modify
     */
    public static ModelPart getFlag() {
        return FLAG;
    }

    /**
     * @param graphics the GUI graphics for rendering it internal
     * @param x        the x position of the banner
     * @param y        the y position of the banner
     * @param patterns the patterns to render
     * @param height   the height and scale of the banner
     */
    public static void renderBanner(GuiGraphics graphics, float x, float y, BannerPatternLayers patterns, DyeColor baseColor, int height) {
        MultiBufferSource.BufferSource source = MINECRAFT.renderBuffers().bufferSource();
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y + height, 0.0F);
        float scale = height / 40f;
        poseStack.scale(24.0F * scale, -24.0F * scale, 1.0F);
        poseStack.translate(0.5F, 0.5F, 0);
        float f = 2 / 3f;
        poseStack.scale(f, -f, -f);
        BannerRenderer.renderPatterns(poseStack, source, 15728880, OverlayTexture.NO_OVERLAY, FLAG, ModelBakery.BANNER_BASE, true, baseColor, patterns);
        poseStack.popPose();
        source.endBatch();
    }

    public static void renderBannerFromStack(GuiGraphics graphics, int x, int y, ItemStack stack, int height) {
        renderBanner(graphics, x, y, fromStack(stack), ((BannerItem) stack.getItem()).getColor(), height);
    }

    public static BannerPatternLayers fromStack(ItemStack bannerStack) {
        return bannerStack.get(DataComponents.BANNER_PATTERNS);
    }
}
