package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.FinalizerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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

        @Override
        public void toNw(FriendlyByteBuf buf, RemoveParticleFinalizer val) {

        }

        @Override
        public RemoveParticleFinalizer fromNw(FriendlyByteBuf buf, ClientLevel level) {
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
        return "REMOVE";
    }
}
