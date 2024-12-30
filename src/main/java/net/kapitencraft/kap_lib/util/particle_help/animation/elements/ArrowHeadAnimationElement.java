package net.kapitencraft.kap_lib.util.particle_help.animation.elements;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.util.particle_help.ParticleAmountHolder;
import net.kapitencraft.kap_lib.util.particle_help.ParticleGradient;
import net.kapitencraft.kap_lib.util.particle_help.ParticleGradientHolder;
import net.kapitencraft.kap_lib.util.particle_help.animation.ParticleAnimator;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class ArrowHeadAnimationElement extends ParticleAnimationElement {
    private final ParticleGradientHolder[] holders;

    public ArrowHeadAnimationElement(String id, int amount, ParticleOptions options1, ParticleOptions options2, int maxSteps, int maxParticles) {
        this(id, amount, new ParticleGradient(maxSteps, maxParticles, options1, options2).generate());
    }

    private ArrowHeadAnimationElement(String id, int amount, ParticleGradientHolder[] holders) {
        super(amount, id);
        this.holders = holders;
    }

    @Override
    public boolean tick(ParticleAnimator animator) {
        for (int y = 0; y < 2; y++) {
            for (int i = y; i < 10; i++) {
                Vec3 targetLoc = MathHelper.calculateViewVector(animator.target.getXRot(), animator.target.getYRot() + (y == 0 ? 160 : -160)).scale(i * 0.05).add(animator.target.getX(), animator.target.getY() + 0.1f, animator.target.getZ());
                ParticleAmountHolder holder = holders[i].holder1();
                for (int j = 0; j < holder.amount(); j++) {
                    animator.target.level().addParticle(holder.particleType(), targetLoc.x, targetLoc.y, targetLoc.z, 0, 0, 0);
                }
                holder = holders[i].holder2();
                for (int j = 0; j < holder.amount(); j++) {
                    animator.target.level().addParticle(holder.particleType(), targetLoc.x, targetLoc.y, targetLoc.z, 0, 0, 0);
                }
            }
        }

        return false;
    }

    @Override
    public void writeToNW(FriendlyByteBuf buf) {
        buf.writeInt(this.getAmount());
        buf.writeUtf(this.getId());
        buf.writeInt(holders.length);
        for (ParticleGradientHolder holder : holders) {
            holder.toNW(buf);
        }
    }

    @Override
    public ParticleAnimationElement readFromNW(FriendlyByteBuf buf) {
        int valAmount = buf.readInt();
        String name = buf.readUtf();
        int amount = buf.readInt();
        ParticleGradientHolder[] holders = new ParticleGradientHolder[amount];
        for (int i = 0; i < amount; i++) {
            holders[i] = ParticleGradientHolder.fromNW(buf);
        }
        return new ArrowHeadAnimationElement(name, valAmount, holders);
    }
}
