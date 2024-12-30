package net.kapitencraft.kap_lib.util.particle_help.animation;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * class to add particle animations to entities
 */
@OnlyIn(Dist.CLIENT)
public interface IAnimatable {
    ParticleAnimator getAnimator();

    static ParticleAnimator get(Entity entity) {
        return ((IAnimatable) entity).getAnimator();
    }
}
