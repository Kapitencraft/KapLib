package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.FinalizerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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
        public StreamCodec<? super RegistryFriendlyByteBuf, EmptyFinalizer> codec() {
            return STREAM_CODEC;
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
        return "EMPTY";
    }
}
