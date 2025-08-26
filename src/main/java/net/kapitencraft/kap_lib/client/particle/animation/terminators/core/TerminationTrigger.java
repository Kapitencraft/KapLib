package net.kapitencraft.kap_lib.client.particle.animation.terminators.core;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimationManager;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;

public interface TerminationTrigger<T extends TerminationTriggerInstance> {

    void addListener(ParticleAnimator animator, Listener<T> terminator);

    void removeListener(ParticleAnimator animator, Listener<T> terminator);

    void clearListeners(ParticleAnimator animator);

    void toNw(FriendlyByteBuf buf, T terminator);

    T fromNw(FriendlyByteBuf buf);

    static <T extends TerminationTriggerInstance> void writeToNw(FriendlyByteBuf buf, T terminator) {
        TerminationTrigger<T> trigger = (TerminationTrigger<T>) terminator.getTrigger();
        buf.writeRegistryIdUnsafe(ExtraRegistries.TERMINATION_TRIGGERS, trigger);
        trigger.toNw(buf, terminator);
    }

    static TerminationTriggerInstance readFromNw(FriendlyByteBuf buf) {
        TerminationTrigger<TerminationTriggerInstance> trigger = (TerminationTrigger<TerminationTriggerInstance>) buf.readRegistryIdUnsafe(ExtraRegistries.TERMINATION_TRIGGERS);
        return trigger.fromNw(buf);
    }

    record Listener<T extends TerminationTriggerInstance>(T trigger, ParticleAnimator animator) {

        public void run(ParticleAnimationManager manager) {
            manager.remove(animator);
        }

            public boolean isFor(ParticleAnimator animator) {
                return this.animator == animator;
            }
        }
}
