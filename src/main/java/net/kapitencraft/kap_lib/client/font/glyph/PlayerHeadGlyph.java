package net.kapitencraft.kap_lib.client.font.glyph;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.joml.Matrix4f;

public class PlayerHeadGlyph extends BakedGlyph {
    PlayerInfo playerInfo;

    public PlayerHeadGlyph(GlyphRenderTypes pRenderTypes, float pU0, float pU1, float pV0, float pV1, float pLeft, float pRight, float pUp, float pDown) {
        super(pRenderTypes, pU0, pU1, pV0, pV1, pLeft, pRight, pUp, pDown);
    }

    @Override
    public void render(boolean pItalic, float pX, float pY, Matrix4f pMatrix, VertexConsumer pBuffer, float pRed, float pGreen, float pBlue, float pAlpha, int pPackedLight) {
        int i = 3;
        float f = pX + this.left;
        float f1 = pX + this.right;
        float f2 = this.up - 3.0F;
        float f3 = this.down - 3.0F;
        float f4 = pY + f2;
        float f5 = pY + f3;
        float f6 = pItalic ? 1.0F - 0.25F * f2 : 0.0F;
        float f7 = pItalic ? 1.0F - 0.25F * f3 : 0.0F;
        RenderSystem.setShaderTexture(0, playerInfo.getSkinLocation());
        pBuffer.vertex(pMatrix, f + f6, f4, 0.0F).color(pRed, pGreen, pBlue, pAlpha).uv(8, 8).uv2(pPackedLight).endVertex();
        pBuffer.vertex(pMatrix, f + f7, f5, 0.0F).color(pRed, pGreen, pBlue, pAlpha).uv(8, 16).uv2(pPackedLight).endVertex();
        pBuffer.vertex(pMatrix, f1 + f7, f5, 0.0F).color(pRed, pGreen, pBlue, pAlpha).uv(16, 16).uv2(pPackedLight).endVertex();
        pBuffer.vertex(pMatrix, f1 + f6, f4, 0.0F).color(pRed, pGreen, pBlue, pAlpha).uv(16, 8).uv2(pPackedLight).endVertex();
    }
}
