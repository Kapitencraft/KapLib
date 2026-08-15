package net.kapitencraft.kap_lib.particle.animation.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class ParticleSpawnSink implements BiConsumer<Vec3, String> {

    private final ParticleEngine engine;
    private final ClientLevel level;
    private final ParticleAnimator animator;
    public final RandomSource random;
    private final ParticleAnimation animation;

    public ParticleSpawnSink(ParticleAnimator animator, ParticleAnimation animation) {
        this.engine = Minecraft.getInstance().particleEngine;
        this.level = Minecraft.getInstance().level;
        this.animator = animator;
        this.animation = animation;
        this.random = RandomSource.create();
    }

    /**
     * adds a new particle to the underlying Animator
     *
     * @param x the x,
     * @param y the y,
     * @param z and the z coordinate to add the particle at
     */
    public void accept(double x, double y, double z, String entry) {
        AnimationParticle particle = new AnimationParticle(this.level, animation, x, y, z, 0, 0, 0);
        particle.setTexture(this.animator.getTexture(entry));
        engine.add(particle);
        animator.addParticle(particle);
    }

    /**
     * overload function which swaps the 3 individual dimension parameters for a single vector parameter
     *
     * @param pos the second input argument
     */
    public void accept(Vec3 pos, String entry) {
        this.accept(pos.x, pos.y, pos.z, entry);
    }
}
