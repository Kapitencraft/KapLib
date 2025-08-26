package net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleActivationTrigger<T extends TriggerInstance> implements ActivationTrigger<T> {
    private final List<T> listeners = new ArrayList<>();

    @Override
    public void addListener(Listener<T> instance) {
        listeners.add(instance.getTrigger());
    }

    @Override
    public void removeListener(Listener<T> instance) {
        listeners.remove(instance.getTrigger());
    }
}
