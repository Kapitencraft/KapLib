package net.kapitencraft.kap_lib.client.particle.animation.terminators.core;

public interface TerminationTriggerInstance {
    TerminationTrigger<? extends TerminationTriggerInstance> getTrigger();
}
