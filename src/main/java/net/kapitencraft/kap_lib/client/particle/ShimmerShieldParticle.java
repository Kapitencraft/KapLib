package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ShimmerShieldParticle extends TextureSheetParticle {
    private final List<ShieldElement> elements = new ArrayList<>();
    private final Color min, max;
    private final Entity target;
    private final int maxElementsAmount,
            minRegenTime, maxRegenTime,
            maxLifeTime, minLifeTime;
    private int spawnTime;
    private final float maxSpeed;

    protected ShimmerShieldParticle(ClientLevel pLevel, Color min, Color max, Entity target, int maxElementsAmount, int minRegenTime, int maxRegenTime, float maxSpeed, int maxLifeTime, int minLifeTime) {
        super(pLevel, 0, 0, 0); //location ignored
        this.min = min;
        this.max = max;
        this.target = target;
        this.maxElementsAmount = maxElementsAmount;
        this.minRegenTime = minRegenTime;
        this.maxRegenTime = maxRegenTime;
        this.maxSpeed = maxSpeed;
        this.maxLifeTime = maxLifeTime;
        this.minLifeTime = minLifeTime;
        this.fillRandom();
    }

    private void fillRandom() {
        for (int i = 0; i < maxElementsAmount; i++) {
            this.elements.add(new ShieldElement());
        }
        this.elements.forEach(element -> element.age = this.random.nextIntBetweenInclusive(0, element.lifeTime));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        elements.removeIf(ShieldElement::tick);
        if (this.spawnTime-- <= 0 && this.elements.size() < this.maxElementsAmount) {
            this.elements.add(new ShieldElement());

            if (elements.size() < this.maxElementsAmount) {
                this.spawnTime = this.random.nextIntBetweenInclusive(minRegenTime, maxRegenTime);
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        this.elements.forEach(shieldElement -> shieldElement.render(pBuffer, pRenderInfo.getPosition(), pPartialTicks));
    }

    @Override
    public @NotNull AABB getBoundingBox() {
        return this.target == null ? super.getBoundingBox() : this.target.getBoundingBox().inflate(this.target.getBbWidth() * 0.4, this.target.getBbHeight() * 0.4, this.target.getBbWidth() * 0.4);
    }

    private class ShieldElement {
        private int age;
        private final int lifeTime;
        private final Dimension x, y, z;

        private ShieldElement(int lifeTime, Dimension x, Dimension y, Dimension z) {
            this.lifeTime = lifeTime;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private ShieldElement() {
            this(random.nextIntBetweenInclusive(minLifeTime, maxLifeTime),
                    Dimension.random(random, maxSpeed),
                    Dimension.random(random, maxSpeed),
                    Dimension.random(random, maxSpeed)
            );
        }

        private void render(VertexConsumer pBuffer, Vec3 camPos, float pPartialTicks) {
            Vec3 origin = target.position().add(0, target.getBbHeight() / 2, 0);
            Vec3 pos = origin.add(offset(pPartialTicks));
            Vec2 rot = MathHelper.createTargetRotationFromPos(pos, origin);
            Quaternionf quaternionf = new Quaternionf(rot.x, rot.y, 0, 1);

            Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

            Vec3 relative = pos.subtract(camPos);
            for(int i = 0; i < 4; ++i) {
                Vector3f vector3f = avector3f[i];
                vector3f.rotate(quaternionf);
                vector3f.mul(getQuadSize(pPartialTicks));
                vector3f.add(relative.toVector3f());
            }

            Color color = max.mix(min, (Math.max(this.age - 1, 0) + pPartialTicks) / this.lifeTime);
            float f6 = getU0();
            float f7 = getU1();
            float f4 = getV0();
            float f5 = getV1();
            int light = 15728880;
            pBuffer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(f7, f5).setColor(color.r, color.g, color.b, color.a).setLight(light);
            pBuffer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(f7, f4).setColor(color.r, color.g, color.b, color.a).setLight(light);
            pBuffer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(f6, f4).setColor(color.r, color.g, color.b, color.a).setLight(light);
            pBuffer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(f6, f5).setColor(color.r, color.g, color.b, color.a).setLight(light);
        }


        private boolean tick() {
            this.x.tick();
            this.y.tick();
            this.z.tick();
            return this.age++ >= this.lifeTime;
        }

        private Vec3 offset(float partialTick) {
            return new Vec3(
                    this.x.getOffset(partialTick)*.8*target.getBbWidth(),
                    this.y.getOffset(partialTick)*.8*target.getBbHeight(),
                    this.z.getOffset(partialTick)*.8*target.getBbWidth()
            );
        }

        private static class Dimension {
            private final float speed;
            private float pos, oPos;

            private Dimension(float speed, float pos) {
                this.speed = speed;
                this.pos = pos;
            }

            private static Dimension random(RandomSource source, float maxSpeed) {
                return new Dimension(MathHelper.randomBetween(source, -maxSpeed, maxSpeed), MathHelper.randomBetween(source, 0, 360));
            }

            private float getOffset(float partialTick) {
                return Mth.sin(Mth.lerp(partialTick, this.oPos, this.pos));
            }

            private void tick() {
                this.oPos = pos;
                this.pos += speed;
            }
        }
    }

    public static class Provider implements ParticleProvider.Sprite<ShimmerShieldParticleOptions> {

        public Provider() {
        }

        @Nullable
        @Override
        public TextureSheetParticle createParticle(ShimmerShieldParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ShimmerShieldParticle(pLevel, pType.getMinColor(), pType.getMaxColor(), pLevel.getEntity(pType.getEntityId()), pType.getMaxElements(), pType.getMinRegenTime(), pType.getMaxRegenTime(), pType.getMaxSpeed(), pType.getMaxLifeTime(), pType.getMinLifeTime());
        }
    }
}