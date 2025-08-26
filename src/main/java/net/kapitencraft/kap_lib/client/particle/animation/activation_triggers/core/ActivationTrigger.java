package net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ActivationTrigger<T extends TriggerInstance> {

    void addListener(Listener<T> instance);

    void removeListener(Listener<T> instance);

    boolean active(Listener<T> instance);

    void toNw(FriendlyByteBuf buf, T val);

    T fromNw(FriendlyByteBuf buf);

    static <T extends TriggerInstance> T readFromNw(FriendlyByteBuf buf) {
        ActivationTrigger<T> trigger = (ActivationTrigger<T>) buf.readRegistryIdUnsafe(ExtraRegistries.ACTIVATION_TRIGGERS);
        return trigger.fromNw(buf);
    }

    static <T extends TriggerInstance> void writeToNw(FriendlyByteBuf buf, T val) {
        ActivationTrigger<T> trigger = (ActivationTrigger<T>) val.getTrigger();
        buf.writeRegistryIdUnsafe(ExtraRegistries.ACTIVATION_TRIGGERS, trigger);
        trigger.toNw(buf, val);
    }

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
