package net.kapitencraft.kap_lib.client.particle.animation.terminators.core;

import com.google.common.collect.Sets;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public abstract class SimpleTerminationTrigger<T extends TerminationTriggerInstance> implements TerminationTrigger<T> {
    private final Set<TerminationTrigger.Listener<T>> terminators = Sets.newHashSet();

    @Override
    public final void addListener(ParticleAnimator terminators, Listener<T> terminator) {
        this.terminators.add(terminator);
    }

    @Override
    public final void removeListener(ParticleAnimator animator, Listener<T> terminator) {
        this.terminators.remove(terminator);
    }

    @Override
    public final void clearListeners(ParticleAnimator animator) {
        this.terminators.removeIf(tListener -> tListener.isFor(animator));
    }

    /**
     * triggers a check for this termination trigger
     * @param instanceFilter filter the instances use to check whether they are affected by the trigger
     */
    protected void trigger(Predicate<T> instanceFilter) {
        List<Listener<T>> list = null;
        for (Listener<T> listener : terminators) {
            T t = listener.trigger();
            if (instanceFilter.test(t)) {
                if (list == null) list = Lists.newArrayList();
                list.add(listener);
            }
        }
        if (list != null) {
            for (Listener<T> listener : list) {
                listener.run(LibClient.animations);
                this.terminators.remove(listener);
            }
        }
    }
}
