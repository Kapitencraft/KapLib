package net.kapitencraft.kap_lib.particle.animation.terminators;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Set;

public class TimedTerminator implements TerminationTrigger<TimedTerminator.Instance> {
    private static final MapCodec<Instance> CODEC = Codec.INT.xmap(Instance::new, Instance::duration).fieldOf("duration");
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
    public StreamCodec<? super RegistryFriendlyByteBuf, Instance> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<Instance> codec() {
        return CODEC;
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
            listener.run(ClientParticleAnimationManager.INSTANCE);
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
