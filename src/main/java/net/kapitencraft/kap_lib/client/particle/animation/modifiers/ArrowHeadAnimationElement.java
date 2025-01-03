package net.kapitencraft.kap_lib.client.particle.animation.modifiers;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.util.particle_help.ParticleAmountHolder;
import net.kapitencraft.kap_lib.util.particle_help.ParticleGradientHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Deprecated
public class ArrowHeadAnimationElement {
    private final ParticleGradientHolder[] holders = new ParticleGradientHolder[0];

    public boolean tick(LivingEntity target) {
        for (int y = 0; y < 2; y++) {
            for (int i = y; i < 10; i++) {
                Vec3 targetLoc = MathHelper.calculateViewVector(target.getXRot(), target.getYRot() + (y == 0 ? 160 : -160)).scale(i * 0.05).add(target.getX(), target.getY() + 0.1f, target.getZ());
                ParticleAmountHolder holder = holders[i].holder1();
                for (int j = 0; j < holder.amount(); j++) {
                    target.level().addParticle(holder.particleType(), targetLoc.x, targetLoc.y, targetLoc.z, 0, 0, 0);
                }
                holder = holders[i].holder2();
                for (int j = 0; j < holder.amount(); j++) {
                    target.level().addParticle(holder.particleType(), targetLoc.x, targetLoc.y, targetLoc.z, 0, 0, 0);
                }
            }
        }

        return false;
    }
}
