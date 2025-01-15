package net.kapitencraft.kap_lib.client.particle.animation.core;

import net.kapitencraft.kap_lib.client.particle.animation.spawners.VisibleSpawner;
import net.minecraft.CrashReport;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class ParticleAnimator {
    /**
     * the animation this animator is running
     */
    private final ParticleAnimation animation;
    /**
     * the particles that are effected by the animation
     */
    private final List<ParticleConfig> particles = new ArrayList<>();
    /**
     * the spawn sink used inside {@link VisibleSpawner#spawn(ParticleSpawnSink) Spawner#spawn} to add new particles
     */
    private final ParticleSpawnSink sink = new ParticleSpawnSink(this);
    /**
     * the amount of ticks this animator has been running for
     */
    public int runningTicks;
    /**
     * the delay in ticks until the next particle spawn will occur
     */
    private int currentSpawnDelay;

    @ApiStatus.Internal
    public ParticleAnimator(ParticleAnimation animation) {
        this.animation = animation;
    }


    @ApiStatus.Internal
    public void addParticle(Particle particle) {
        particles.add(new ParticleConfig(particle, animation));
    }

    @ApiStatus.Internal
    public void tick(RandomSource source) {
        if (currentSpawnDelay == 0) {
            animation.spawnTick(sink);
            currentSpawnDelay = Mth.randomBetweenInclusive(source, animation.minSpawnDelay, animation.maxSpawnDelay);
        }
        currentSpawnDelay--;
        List<ParticleConfig> expired = particles.stream().filter(ParticleConfig::hasExpired).toList();
        expired.forEach(ParticleConfig::invalidate);
        particles.removeAll(expired);
        particles.forEach(ParticleConfig::tick);
        runningTicks++;
    }

    @ApiStatus.Internal
    public boolean beenTerminated() {
        return this.animation.terminated(this);
    }

    public void fillCrashReport(CrashReport report) {
        report.addCategory("Animator")
                .setDetail("runningTicks", this.runningTicks)
                .setDetail("currentSpawnDelay", this.currentSpawnDelay)
                .setDetail("Particles", this.particles);
        this.animation.fillCrashReport(report);
    }
}
