package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.FinalizerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class RemoveParticleFinalizer implements ParticleFinalizer {
    private static final RemoveParticleFinalizer INSTANCE = new RemoveParticleFinalizer();

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Type getType() {
        return FinalizerTypes.REMOVE_PARTICLE.get();
    }

    @Override
    public void finalize(ParticleConfig config) {
        config.removeTarget();
    }

    public static class Type implements ParticleFinalizer.Type<RemoveParticleFinalizer> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, RemoveParticleFinalizer> STREAM_CODEC = StreamCodec.unit(INSTANCE);
        private static final MapCodec<RemoveParticleFinalizer> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RemoveParticleFinalizer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<RemoveParticleFinalizer> codec() {
            return CODEC;
        }
    }

    public static class Builder implements ParticleFinalizer.Builder {

        @Override
        public ParticleFinalizer build() {
            return INSTANCE;
        }
    }

    @Override
    public String toString() {
        return "REMOVE";
    }
}
