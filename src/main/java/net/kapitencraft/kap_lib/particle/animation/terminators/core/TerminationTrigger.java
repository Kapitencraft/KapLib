package net.kapitencraft.kap_lib.particle.animation.terminators.core;

import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface TerminationTrigger<T extends TerminationTriggerInstance> {

    void addListener(ParticleAnimator animator, Listener<T> terminator);

    void removeListener(ParticleAnimator animator, Listener<T> terminator);

    void clearListeners(ParticleAnimator animator);

    StreamCodec<RegistryFriendlyByteBuf, TerminationTriggerInstance> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.TERMINATOR_TYPES).dispatch(TerminationTriggerInstance::getTrigger, TerminationTrigger::codec);

    StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

    record Listener<T extends TerminationTriggerInstance>(T trigger, ParticleAnimator animator) {

        public void run(ParticleAnimationManager manager) {
            manager.remove(animator);
        }

            public boolean isFor(ParticleAnimator animator) {
                return this.animator == animator;
            }
        }
}
