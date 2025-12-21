package net.kapitencraft.kap_lib.particle.animation.finalizers;

import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ParticleFinalizer {
    StreamCodec<RegistryFriendlyByteBuf, ParticleFinalizer> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.FINALIZER_TYPES).dispatch(ParticleFinalizer::getType, Type::codec);

    @NotNull Type<? extends ParticleFinalizer> getType();

    void finalize(ParticleConfig config);

    interface Type<T extends ParticleFinalizer> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }

    interface Builder {

        ParticleFinalizer build();
    }
}
