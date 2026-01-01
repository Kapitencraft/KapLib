package net.kapitencraft.kap_lib.particle.animation.activation_triggers.core;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ActivationTrigger<T extends TriggerInstance> {

    void addListener(Listener<T> instance);

    void removeListener(Listener<T> instance);

    boolean active(Listener<T> instance);

    StreamCodec<RegistryFriendlyByteBuf, TriggerInstance> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.ACTIVATION_TRIGGERS).dispatch(TriggerInstance::getTrigger, ActivationTrigger::codec);

    StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

    @OnlyIn(Dist.CLIENT)
    class Listener<T extends TriggerInstance> {
        private final T trigger;
        private final ParticleAnimator animator;

        public Listener(T trigger, ParticleAnimator animator) {
            this.trigger = trigger;
            this.animator = animator;
        }

        public void run() {
            ParticleAnimationManager.INSTANCE.triggerComplete(animator, trigger);
        }

        public T getTrigger() {
            return trigger;
        }
    }
}
