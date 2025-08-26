package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.FinalizerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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

        @Override
        public void toNw(FriendlyByteBuf buf, EmptyFinalizer val) {

        }

        @Override
        public EmptyFinalizer fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return INSTANCE;
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
