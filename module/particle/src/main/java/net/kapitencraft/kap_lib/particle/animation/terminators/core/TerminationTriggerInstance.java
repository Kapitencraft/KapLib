package net.kapitencraft.kap_lib.particle.animation.terminators.core;

public interface TerminationTriggerInstance {
    TerminationTrigger<? extends TerminationTriggerInstance> getTrigger();
}
