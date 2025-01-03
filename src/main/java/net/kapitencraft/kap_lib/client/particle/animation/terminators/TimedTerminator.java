package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class TimedTerminator implements AnimationTerminator {
    private final int ticks;

    public TimedTerminator(int ticks) {
        this.ticks = ticks;
    }

    public static AnimationTerminator.Builder ticks(int tickCount) {
        return new Builder().ticks(tickCount);
    }

    @Override
    public @NotNull Type getType() {
        return TerminatorTypes.TIMED.get();
    }

    @Override
    public boolean shouldTerminate(ParticleAnimator animator) {
        return animator.runningTicks > ticks;
    }

    public static class Type implements AnimationTerminator.Type<TimedTerminator> {

        @Override
        public void toNw(FriendlyByteBuf buf, TimedTerminator val) {
            buf.writeInt(val.ticks);
        }

        @Override
        public TimedTerminator fromNw(FriendlyByteBuf buf) {
            return new TimedTerminator(buf.readInt());
        }
    }

    public static class Builder implements AnimationTerminator.Builder {
        private int ticks;

        @Override
        public AnimationTerminator build() {
            return new TimedTerminator(ticks);
        }

        public Builder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }
    }
}
