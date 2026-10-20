package net.kapitencraft.kap_lib.particle.animation.core;

import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.animation.spawners.VisibleSpawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;
import net.minecraft.CrashReport;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<TerminationTriggerInstance> terminators = animation.getTerminators();
        for (TerminationTriggerInstance terminator : terminators) {
            TerminationTrigger<TerminationTriggerInstance> trigger = (TerminationTrigger<TerminationTriggerInstance>) terminator.getTrigger();
            trigger.addListener(this, new TerminationTrigger.Listener<>(terminator, this));
        }
    }


    @ApiStatus.Internal
    public void addParticle(Particle particle) {
        //LibClient.animations.addContained(particle);
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

    public void fillCrashReport(CrashReport report) {
        report.addCategory("Animator")
                .setDetail("runningTicks", this.runningTicks)
                .setDetail("currentSpawnDelay", this.currentSpawnDelay)
                .setDetail("Particles", this.particles);
        this.animation.fillCrashReport(report);
    }

    public void removed() {
        //finalize all remaining particles
        this.particles.forEach(ParticleConfig::invalidate);
    }

    public static class Pending {
        private final ParticleAnimator animator;
        private final Map<ActivationTriggerInstance, ActivationTrigger.Listener<?>> reqs;

        public Pending(ParticleAnimator animator, Map<ActivationTriggerInstance, ActivationTrigger.Listener<?>> reqs) {
            this.animator = animator;
            this.reqs = reqs;
        }

        public ParticleAnimator getAnimator() {
            return animator;
        }

        public void removeTrigger(ActivationTriggerInstance instance) {
            rmT(instance.getTrigger(), reqs.get(instance));
        }

        private <T extends ActivationTriggerInstance> void rmT(ActivationTrigger<T> trigger, ActivationTrigger.Listener<?> listener) {
            trigger.removeListener((ActivationTrigger.Listener<T>) listener);
        }
    }
}
