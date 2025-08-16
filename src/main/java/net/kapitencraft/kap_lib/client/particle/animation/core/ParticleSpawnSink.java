package net.kapitencraft.kap_lib.client.particle.animation.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class ParticleSpawnSink implements BiConsumer<ParticleOptions, Vec3> {

    private final ParticleEngine engine;
    private final ParticleAnimator animator;
    public final RandomSource random;

    public ParticleSpawnSink(ParticleAnimator animator) {
        this.engine = Minecraft.getInstance().particleEngine;
        this.animator = animator;
        this.random = RandomSource.create();
    }

    /**
     * adds a new particle to the underlying Animator
     * @param particleConfig the particle to add
     * @param x the x,
     * @param y the y,
     * @param z and the z coordinate to add the particle at
     */
    public void accept(ParticleOptions particleConfig, double x, double y, double z) {
        Particle particle = engine.createParticle(particleConfig, 0, 0, 0, 0, 0, 0);
        if (particle != null) {
            particle.setPos(x, y, z);
            particle.xd = 0;
            particle.yd = 0;
            particle.zd = 0;
            animator.addParticle(particle);
        }
    }

    /**
     * overload function which changes
     * @param particleConfig the first input argument
     * @param pos            the second input argument
     */
    public void accept(ParticleOptions particleConfig, Vec3 pos) {
        this.accept(particleConfig, pos.x, pos.y, pos.z);
    }
}
