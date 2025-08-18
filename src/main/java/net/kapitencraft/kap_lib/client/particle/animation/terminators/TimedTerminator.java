package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import com.google.common.collect.Sets;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Set;

public class TimedTerminator implements TerminationTrigger<TimedTerminator.Instance> {
    private static final StreamCodec<? super RegistryFriendlyByteBuf, Instance> STREAM_CODEC = ByteBufCodecs.INT.map(Instance::new, Instance::duration);

    private final Set<Listener<TimedTerminator.Instance>> listeners = Sets.newHashSet();

    public static TerminationTriggerInstance seconds(int i) {
        return new Instance(i * 20);
    }

    public static TerminationTriggerInstance ticks(int ticks) {
        return new Instance(ticks);
    }

    @Override
    public void addListener(ParticleAnimator animator, Listener<Instance> terminator) {
        listeners.add(terminator);
    }

    @Override
    public void removeListener(ParticleAnimator animator, Listener<Instance> terminator) {
        listeners.remove(terminator);
    }

    @Override
    public void clearListeners(ParticleAnimator animator) {
        this.listeners.removeIf(tListener -> tListener.isFor(animator));
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, Instance> codec() {
        return STREAM_CODEC;
    }

    public void trigger() {
        List<Listener<TimedTerminator.Instance>> list = null;
        for (Listener<TimedTerminator.Instance> listener : listeners) {
            if (listener.trigger().duration <= listener.animator().runningTicks) {
                if (list == null) list = Lists.newArrayList();
                list.add(listener);
            }
        }
        if (list != null) for (Listener<TimedTerminator.Instance> listener : list) {
            listener.run(LibClient.animations);
            this.listeners.remove(listener);
        }
    }

    public record Instance(int duration) implements TerminationTriggerInstance {
        @Override
        public TerminationTrigger<? extends TerminationTriggerInstance> getTrigger() {
            return TerminatorTriggers.TIMED.get();
        }
    }
}
