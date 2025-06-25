package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.SimpleTerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.network.FriendlyByteBuf;

public class TimedTerminator extends SimpleTerminationTrigger<TimedTerminator.Instance> {
    public static Instance ticks(int tickCount) {
        return new Instance();
    }

    public static Instance seconds(int secondsCount) {
        return ticks(secondsCount * 20);
    }

    @Override
    public void toNw(FriendlyByteBuf buf, Instance terminator) {

    }

    @Override
    public Instance fromNw(FriendlyByteBuf buf) {
        return null;
    }

    public static class Instance implements TerminationTriggerInstance {
        private final int ;

        @Override
        public TerminationTrigger<? extends TerminationTriggerInstance> getTrigger() {
            return TerminatorTriggers.TIMED.get();
        }
    }
}
