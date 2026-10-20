package net.kapitencraft.kap_lib.particle.animation.core;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPreset;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manager of all animations
 */
public final class ClientParticleAnimationManager extends SimpleJsonResourceReloadListener {
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
    private final Map<ParticleAnimator.Pending, List<ActivationTriggerInstance>> onHold = new HashMap<>();

    public ClientParticleAnimationManager() {
        super(JsonHelper.GSON, "animation_presets");
    }

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
            Map<ActivationTriggerInstance, ActivationTrigger.Listener<?>> listeners = new HashMap<>();
            ParticleAnimator.Pending animator = new ParticleAnimator.Pending(new ParticleAnimator(animation), listeners);
            List<ActivationTriggerInstance> remaining = new ArrayList<>();
            for (ActivationTriggerInstance instance : triggers) {
                ActivationTrigger.Listener<ActivationTriggerInstance> listener = new ActivationTrigger.Listener<>(instance, animator);
                addListener(listener, remaining);
                listeners.put(instance, listener);
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

    public void triggerComplete(ParticleAnimator.Pending pending, ActivationTriggerInstance trigger) {
        List<ActivationTriggerInstance> triggers = onHold.get(pending);
        triggers.remove(trigger);
        pending.removeTrigger(trigger);
        if (triggers.isEmpty()) {
            onHold.remove(pending);
            activeAnimations.add(pending.getAnimator());
        }
    }

    public void remove(ParticleAnimator animator) {
        activeAnimations.remove(animator);
        animator.removed();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean usePreset(ResourceLocation location, ParticleAnimationPresetContext data) {
        ParticleAnimationPreset preset = this.presets.get(location);
        if (preset == null)
            return false;
        this.accept(preset.build(data));
        return true;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.presets.clear();
        object.forEach((location, jsonElement) -> {
            DataResult<ParticleAnimationPreset> result = ParticleAnimationPreset.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            result.resultOrPartial(s -> LOGGER.warn("error parsing particle animation preset {}: {}", location, s))
                    .ifPresent(p -> this.presets.put(location, p));
        });
    }
}
