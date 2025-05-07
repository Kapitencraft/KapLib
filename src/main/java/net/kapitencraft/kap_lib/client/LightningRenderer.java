package net.kapitencraft.kap_lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class LightningRenderer {

    //vanilla alpha: .3f
    public static void renderLightning(PoseStack pPoseStack, Vec3 pPos1, Vec3 pPos2, RandomSource pSource, MultiBufferSource pBuffer, float pAlpha) {
        float[] afloat = new float[8];
        float[] afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float)(pSource.nextInt(11) - 5);
            f1 += (float)(pSource.nextInt(11) - 5);
        }

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = pPoseStack.last().pose();

        for(int j = 0; j < 4; ++j) {
            for(int k = 0; k < 3; ++k) {
                int l = 7 - k;
                int i1 = 0;

                if (k > 0) {
                    i1 = l - 2;
                }

                float f2 = afloat[l] - f;
                float f3 = afloat1[l] - f1;

                for(int j1 = l; j1 >= i1; --j1) {
                    float f4 = f2;
                    float f5 = f3;
                    if (k == 0) {
                        f2 += (float)(pSource.nextInt(11) - 5);
                        f3 += (float)(pSource.nextInt(11) - 5);
                    } else {
                        f2 += (float)(pSource.nextInt(31) - 15);
                        f3 += (float)(pSource.nextInt(31) - 15);
                    }

                    float red = 0.45F;
                    float green = 0.45F;
                    float blue = 0.5F;
                    float f10 = 0.1F + (float)j * 0.2F;
                    float f11 = 0.1F + (float)j * 0.2F;

                    if (k == 0) {
                        f10 *= (float)j1 * 0.1F + 1.0F;
                        f11 *= ((float)j1 - 1.0F) * 0.1F + 1.0F;
                    }

                    for (int i = 0; i < 4; i++) {
                        quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, red, green, blue, f10, f11, pAlpha,
                                i != 0 && i != 3, i > 1, i < 2, i != 0 && i != 3
                        );
                    }
                }
            }
        }
    }

    private static void quad(Matrix4f pMatrix, VertexConsumer pConsumer, float pX1, float pZ1, int pIndex, float pX2, float pZ2, float pRed, float pGreen, float pBlue, float pAlpha, float p_115283_, float p_115284_, boolean p_115285_, boolean p_115286_, boolean p_115287_, boolean p_115288_) {
        pConsumer.vertex(pMatrix, pX1 + (p_115285_ ? p_115284_ : -p_115284_), (float)(pIndex * 16), pZ1 + (p_115286_ ? p_115284_ : -p_115284_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(pMatrix, pX2 + (p_115285_ ? p_115283_ : -p_115283_), (float)((pIndex + 1) * 16), pZ2 + (p_115286_ ? p_115283_ : -p_115283_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(pMatrix, pX2 + (p_115287_ ? p_115283_ : -p_115283_), (float)((pIndex + 1) * 16), pZ2 + (p_115288_ ? p_115283_ : -p_115283_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(pMatrix, pX1 + (p_115287_ ? p_115284_ : -p_115284_), (float)(pIndex * 16), pZ1 + (p_115288_ ? p_115284_ : -p_115284_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
    }
}
