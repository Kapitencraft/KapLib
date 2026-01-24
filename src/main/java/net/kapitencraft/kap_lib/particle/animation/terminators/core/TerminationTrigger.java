package net.kapitencraft.kap_lib.particle.animation.terminators.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface TerminationTrigger<T extends TerminationTriggerInstance> {
    Codec<TerminationTriggerInstance> CODEC = ParticleAnimationRegistries.TERMINATION_TRIGGERS.byNameCodec().dispatch(TerminationTriggerInstance::getTrigger, TerminationTrigger::codec);
    StreamCodec<RegistryFriendlyByteBuf, TerminationTriggerInstance> STREAM_CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.TERMINATOR_TYPES).dispatch(TerminationTriggerInstance::getTrigger, TerminationTrigger::streamCodec);

    void addListener(ParticleAnimator animator, Listener<T> terminator);

    void removeListener(ParticleAnimator animator, Listener<T> terminator);

    void clearListeners(ParticleAnimator animator);

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    MapCodec<T> codec();

    record Listener<T extends TerminationTriggerInstance>(T trigger, ParticleAnimator animator) {

        public void run(ClientParticleAnimationManager manager) {
            manager.remove(animator);
        }

        public boolean isFor(ParticleAnimator animator) {
            return this.animator == animator;
        }
    }
}
