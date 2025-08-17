package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.FinalizerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.synth.Region;

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

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RemoveParticleFinalizer> codec() {
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
        return "REMOVE";
    }
}
