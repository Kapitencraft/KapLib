package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.FinalizerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class EmptyFinalizer implements ParticleFinalizer {
    private static final EmptyFinalizer INSTANCE = new EmptyFinalizer();

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Type getType() {
        return FinalizerTypes.EMPTY.get();
    }

    @Override
    public void finalize(ParticleConfig config) {
    }

    public static class Type implements ParticleFinalizer.Type<EmptyFinalizer> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EmptyFinalizer> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EmptyFinalizer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<EmptyFinalizer.Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements ParticleFinalizer.Builder<EmptyFinalizer> {
        private static final MapCodec<Builder> CODEC = MapCodec.unit(new Builder());

        @Override
        public EmptyFinalizer build(ParticleAnimationPresetContext context) {
            return INSTANCE;
        }

        @Override
        public Type type() {
            return FinalizerTypes.EMPTY.get();
        }
    }

    @Override
    public String toString() {
        return "EMPTY";
    }
}
