package net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ActivationTrigger<T extends TriggerInstance> {

    void addListener(Listener<T> instance);

    void removeListener(Listener<T> instance);

    boolean active(Listener<T> instance);

    StreamCodec<RegistryFriendlyByteBuf, TriggerInstance> CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.ACTIVATION_TRIGGERS).dispatch(TriggerInstance::getTrigger, ActivationTrigger::codec);

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
            LibClient.animations.triggerComplete(animator, trigger);
        }

        public T getTrigger() {
            return trigger;
        }
    }
}
