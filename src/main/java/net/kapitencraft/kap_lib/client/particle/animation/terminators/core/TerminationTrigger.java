package net.kapitencraft.kap_lib.client.particle.animation.terminators.core;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimationManager;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface TerminationTrigger<T extends TerminationTriggerInstance> {

    void addListener(ParticleAnimator animator, Listener<T> terminator);

    void removeListener(ParticleAnimator animator, Listener<T> terminator);

    void clearListeners(ParticleAnimator animator);

    StreamCodec<RegistryFriendlyByteBuf, TerminationTriggerInstance> CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.TERMINATOR_TYPES).dispatch(TerminationTriggerInstance::getTrigger, TerminationTrigger::codec);

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
