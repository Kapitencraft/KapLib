package net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core;

public interface TriggerInstance {

    ActivationTrigger<? extends TriggerInstance> getTrigger();
}
