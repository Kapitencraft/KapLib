package net.kapitencraft.kap_lib.particle.animation.activation_triggers.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class SimpleActivationTrigger<T extends ActivationTriggerInstance> implements ActivationTrigger<T> {
    private final List<Listener<T>> listeners = new ArrayList<>();

    @Override
    public void addListener(Listener<T> instance) {
        listeners.add(instance);
    }

    @Override
    public void removeListener(Listener<T> instance) {
        listeners.remove(instance);
    }

    public void trigger(Predicate<T> predicate) {
        for (Listener<T> listener : listeners) {
            if (predicate.test(listener.getTrigger())) {
                listener.run();
            }
        }
    }
}
