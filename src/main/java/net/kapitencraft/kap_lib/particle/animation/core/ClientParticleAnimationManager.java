package net.kapitencraft.kap_lib.particle.animation.core;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPreset;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manager of all animations
 */
//TODO store animations in JSON and load them via reference
public final class ClientParticleAnimationManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ClientParticleAnimationManager INSTANCE = new ClientParticleAnimationManager();

    private final Map<ResourceLocation, ParticleAnimationPreset> presets = new HashMap<>();
    /**
     * running animations
     */
    private final List<ParticleAnimator> activeAnimations = new ArrayList<>();
    //Map<ParticleAnimator, TerminationTriggerInstance[][]> terminators = new HashMap<>();
    /**
     * animations waiting for their activation
     */
    private final Map<ParticleAnimator, List<ActivationTriggerInstance>> onHold = new HashMap<>();

    public static void activate(List<ParticleAnimation> animations) {
        animations.forEach(INSTANCE::accept);
    }

    /**
     * use {@link ParticleAnimation.ParticleAnimationBuilder#register()}<br>
     * or {@link ParticleAnimation.ParticleAnimationBuilder#sendToPlayer(ServerPlayer)}<br>
     * or {@link  ParticleAnimation.ParticleAnimationBuilder#sendToAllPlayers()} to add animations
     */
    @ApiStatus.Internal
    public void accept(ParticleAnimation animation) {
        List<ActivationTriggerInstance> triggers = animation.getTriggers();
        if (!triggers.isEmpty()) {
            ParticleAnimator animator = new ParticleAnimator(animation);
            List<ActivationTriggerInstance> remaining = new ArrayList<>();
            for (ActivationTriggerInstance instance : triggers) {
                ActivationTrigger.Listener<ActivationTriggerInstance> listener = new ActivationTrigger.Listener<>(instance, animator);
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

    @ApiStatus.Internal
    private <T extends ActivationTriggerInstance> void addListener(ActivationTrigger.Listener<T> instance, List<ActivationTriggerInstance> target) {
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

    @ApiStatus.Internal
    public void updatePresets(Map<ResourceLocation, ParticleAnimationPreset> presets) {

    }

    public void triggerComplete(ParticleAnimator animator, ActivationTriggerInstance trigger) {
        List<ActivationTriggerInstance> triggers = onHold.get(animator);
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
