package net.kapitencraft.kap_lib.client.particle.animation.core;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.TriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.*;

/**
 * manager of all animations
 */
public final class ParticleAnimationManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * running animations
     */
    List<ParticleAnimator> activeAnimations = new ArrayList<>();
    //Map<ParticleAnimator, TerminationTriggerInstance[][]> terminators = new HashMap<>();
    /**
     * animations waiting for their activation
     */
    Map<ParticleAnimator, List<TriggerInstance>> onHold = new HashMap<>();

    /**
     * use {@link ParticleAnimation.Builder#register()}<br>
     * or {@link ParticleAnimation.Builder#sendToPlayer(ServerPlayer)}<br>
     * or {@link  ParticleAnimation.Builder#sendToAllPlayers(ServerLevel)} to add animations
     */
    @ApiStatus.Internal
    public void accept(ParticleAnimation animation) {
        TriggerInstance[] triggers = animation.getTriggers();
        if (triggers.length > 0) {
            ParticleAnimator animator = new ParticleAnimator(animation);
            List<TriggerInstance> remaining = new ArrayList<>();
            for (TriggerInstance instance : triggers) {
                ActivationTrigger.Listener<TriggerInstance> listener = new ActivationTrigger.Listener<>(instance, animator);
                addListener(listener, remaining);
            }
            if (!remaining.isEmpty()) {
                onHold.put(animator, remaining);
                return;
            }
        }
        ParticleAnimator animator = new ParticleAnimator(animation);
        activeAnimations.add(animator);
    }

    private <T extends TriggerInstance> void addListener(ActivationTrigger.Listener<T> instance, List<TriggerInstance> target) {
        ActivationTrigger<T> trigger = (ActivationTrigger<T>) instance.getTrigger().getTrigger();
        if (!trigger.active(instance)) {
            trigger.addListener(instance);
            target.add(instance.getTrigger());
        }
    }

    @ApiStatus.Internal
    public void tick(RandomSource source) {
        TerminatorTriggers.TIMED.get().trigger();
        activeAnimations.forEach(pa -> {
            try {
                pa.tick(source);
            } catch (Exception e) {
                CrashReport report = new CrashReport("ParticleAnimation Error", e);
                report.addCategory("Manager")
                        .setDetail("On Hold", this.onHold.size())
                        .setDetail("Active", this.activeAnimations.size());
                pa.fillCrashReport(report);
                throw new ReportedException(report);
            }
        });
    }

    public void triggerComplete(ParticleAnimator animator, TriggerInstance trigger) {
        List<TriggerInstance> triggers = onHold.get(animator);
        triggers.remove(trigger);
        if (triggers.isEmpty()) {
            onHold.remove(animator);
            activeAnimations.add(animator);
        }
    }

    public void remove(ParticleAnimator animator) {
        activeAnimations.remove(animator);
    }
}
