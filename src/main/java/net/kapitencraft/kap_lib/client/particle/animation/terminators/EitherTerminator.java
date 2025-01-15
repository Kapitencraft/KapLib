package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EitherTerminator implements AnimationTerminator {
    private final AnimationTerminator[] terminators;

    public EitherTerminator(AnimationTerminator[] terminators) {
        this.terminators = terminators;
    }

    @Override
    public @NotNull Type getType() {
        return TerminatorTypes.EITHER.get();
    }

    public static Builder with(AnimationTerminator.Builder... terminators) {
        return new Builder().addTerminator(terminators);
    }

    @Override
    public boolean shouldTerminate(ParticleAnimator animation) {
        return Arrays.stream(terminators).anyMatch(t -> t.shouldTerminate(animation));
    }

    public static class Builder implements AnimationTerminator.Builder {
        private final List<AnimationTerminator> terminators = new ArrayList<>();

        public Builder addTerminator(AnimationTerminator.Builder... terminators) {
            Arrays.stream(terminators).map(AnimationTerminator.Builder::build).forEach(this.terminators::add);
            return this;
        }

        @Override
        public AnimationTerminator build() {
            return new EitherTerminator(terminators.toArray(new AnimationTerminator[0]));
        }
    }

    public static class Type implements AnimationTerminator.Type<EitherTerminator> {

        @Override
        public void toNw(FriendlyByteBuf buf, EitherTerminator val) {
            NetworkHelper.writeArray(buf, val.terminators, AnimationTerminator::toNw);
        }

        @Override
        public EitherTerminator fromNw(FriendlyByteBuf buf) {
            return new EitherTerminator(NetworkHelper.readArray(buf, AnimationTerminator[]::new, AnimationTerminator::fromNw));
        }
    }
}
