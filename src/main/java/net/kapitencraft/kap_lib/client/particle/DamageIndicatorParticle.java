package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * particle that shows Damage dealt in different colors depending on the damage type
 */
@SuppressWarnings("ALL")
public class DamageIndicatorParticle extends Particle {
    public static final double MAX_MOVEMENT = 0.35;

    protected DamageIndicatorParticle(ClientLevel level, double x, double y, double z, double amount, double damageType, float rangeOffset) {
        super(level, x, y, z);

        this.text = amount == Float.MAX_VALUE ? "INFINITE" : KapLibMod.doubleFormat(amount);
        this.color = TextHelper.damageIndicatorColorFromDouble(damageType).getColor();
        this.setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        this.darkColor = FastColor.ARGB32.color(255, (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25));
        this.lifetime = ClientModConfig.getIndicatorLifetime();

        this.yd = rangeOffset;
        this.xd = Mth.nextDouble(KapLibMod.RANDOM_SOURCE, -MAX_MOVEMENT, MAX_MOVEMENT) * rangeOffset;
    }

    private static final char CRIT_CHAR = ' '; //TODO add stars around damage if critical

    private float fadeout = -1;
    private float prevFadeout = -1;

    private final Font font = Minecraft.getInstance().font;

    private final String text;
    private final int color;
    private final int darkColor;

    private float visualDY = 0;
    private float prevVisualDY = 0;
    private float visualDX = 0;
    private float prevVisualDX = 0;


    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);

        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevVisualDY, this.visualDY), 0);

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevVisualDX, this.visualDX), 0, 0);

        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 8d, 0);


        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - font.width(text) / 2f;

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int light = LightTexture.FULL_BRIGHT;
        font.drawInBatch(text, x1,
                0, color, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        poseStack.translate(1, 1, +0.03);
        font.drawInBatch(text, x1,
                0, darkColor, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        buffer.endBatch();

        poseStack.popPose();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (lifetime - length) ? ((float) lifetime - this.age) / length : 1;

            this.prevVisualDY = this.visualDY;
            this.visualDY += this.yd;
            this.prevVisualDX = this.visualDX;
            this.visualDX += this.xd;

            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Math.pow(this.visualDX * 1.5, 2) + Math.pow(this.visualDY - 1, 2)) < 1.9 - 1) {
                this.yd = this.yd / 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }

    public static class Provider implements ParticleProvider<DamageIndicatorParticleOptions> {

        public Provider() {

        }

        @Nullable
        @Override
        public DamageIndicatorParticle createParticle(@NotNull DamageIndicatorParticleOptions particleType, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            if (!ClientModConfig.isIndicatorEnabled()) return null;
            return new DamageIndicatorParticle(level, x, y, z, particleType.getDamage(), particleType.getDamageType(), particleType.getRangeOffset());
        }
    }
}
