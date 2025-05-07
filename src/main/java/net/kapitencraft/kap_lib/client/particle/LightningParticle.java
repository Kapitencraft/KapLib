package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightningParticle extends Particle {
    private final Vec3 start, end;
    private final long seed;
    private float alphaO;

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
    };

    protected LightningParticle(ClientLevel pLevel, Vec3 start, Vec3 end) {
        super(pLevel, 0, 0, 0);
        this.start = start;
        this.end = end;
        this.seed = RandomSource.create().nextLong();
        this.alpha = .3f;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        RandomSource pSource = RandomSource.create(this.seed); //TODO cache all vertexes
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
                        quad(pBuffer, f2, f3, j1, f4, f5, red, green, blue, f10, f11, Mth.clamp(pPartialTicks, alphaO, alpha),
                                i != 0 && i != 3, i > 1, i < 2, i != 0 && i != 3
                        );
                    }
                }
            }
        }
    }

    private void quad(VertexConsumer pConsumer, float pX1, float pZ1, int pIndex, float pX2, float pZ2, float pRed, float pGreen, float pBlue, float pAlpha, float p_115283_, float p_115284_, boolean p_115285_, boolean p_115286_, boolean p_115287_, boolean p_115288_) {
        pConsumer.vertex(this.start.x + pX1 + (p_115285_ ? p_115284_ : -p_115284_), this.start.y + (float)(pIndex * 16), this.start.z + pZ1 + (p_115286_ ? p_115284_ : -p_115284_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(this.start.x + pX2 + (p_115285_ ? p_115283_ : -p_115283_), this.start.y + (float)((pIndex + 1) * 16), this.start.z + pZ2 + (p_115286_ ? p_115283_ : -p_115283_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(this.start.x + pX2 + (p_115287_ ? p_115283_ : -p_115283_), this.start.y + (float)((pIndex + 1) * 16), this.start.z + pZ2 + (p_115288_ ? p_115283_ : -p_115283_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pConsumer.vertex(this.start.x + pX1 + (p_115287_ ? p_115284_ : -p_115284_), this.start.y + (float)(pIndex * 16), this.start.z + pZ1 + (p_115288_ ? p_115284_ : -p_115284_)).color(pRed, pGreen, pBlue, pAlpha).endVertex();
    }

    @Override
    public void tick() {
        if (this.age++ > this.lifetime) this.remove();

        this.alphaO = alpha;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Provider implements ParticleProvider<LightningParticleOptions> {

        @Override
        public @Nullable Particle createParticle(LightningParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new LightningParticle(pLevel, pType.getStart(), pType.getEnd());
        }
    }
}
