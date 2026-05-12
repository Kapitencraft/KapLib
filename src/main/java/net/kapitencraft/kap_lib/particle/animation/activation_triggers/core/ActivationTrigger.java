package net.kapitencraft.kap_lib.particle.animation.activation_triggers.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ActivationTrigger<T extends ActivationTriggerInstance> {
    Codec<ActivationTriggerInstance> CODEC = ParticleAnimationRegistries.ACTIVATION_TRIGGERS.byNameCodec().dispatch(ActivationTriggerInstance::getTrigger, ActivationTrigger::codec);
    StreamCodec<RegistryFriendlyByteBuf, ActivationTriggerInstance> STREAM_CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.ACTIVATION_TRIGGERS).dispatch(ActivationTriggerInstance::getTrigger, ActivationTrigger::streamCodec);


    void addListener(Listener<T> instance);

    void removeListener(Listener<T> instance);

    boolean active(Listener<T> instance);

    MapCodec<T> codec();
    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    @OnlyIn(Dist.CLIENT)
    class Listener<T extends ActivationTriggerInstance> {
        private final T trigger;
        private final ParticleAnimator animator;

        public Listener(T trigger, ParticleAnimator animator) {
            this.trigger = trigger;
            this.animator = animator;
        }

        public void run() {
            ClientParticleAnimationManager.INSTANCE.triggerComplete(animator, trigger);
        }

        public T getTrigger() {
            return trigger;
        }
    }
}
