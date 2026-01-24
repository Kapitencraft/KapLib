package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ParticleFinalizer {
    Codec<ParticleFinalizer> CODEC = ParticleAnimationRegistries.PARTICLE_FINALIZER_TYPES.byNameCodec().dispatch(ParticleFinalizer::getType, Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, ParticleFinalizer> STREAM_CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.FINALIZER_TYPES).dispatch(ParticleFinalizer::getType, Type::streamCodec);

    @NotNull Type<? extends ParticleFinalizer> getType();

    void finalize(ParticleConfig config);

    interface Type<T extends ParticleFinalizer> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
        MapCodec<T> codec();
    }

    interface Builder {

        ParticleFinalizer build();
    }
}
