package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ParticleFinalizer {
    Codec<ParticleFinalizer.Builder<?>> CODEC = ParticleAnimationRegistries.PARTICLE_FINALIZER_TYPES.byNameCodec().dispatch(ParticleFinalizer.Builder::type, Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, ParticleFinalizer> STREAM_CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.FINALIZER_TYPES).dispatch(ParticleFinalizer::getType, Type::streamCodec);

    @NotNull Type<? extends ParticleFinalizer> getType();

    void finalize(ParticleConfig config);

    interface Type<T extends ParticleFinalizer> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
        MapCodec<? extends Builder<T>> codec();
    }

    interface Builder<T extends ParticleFinalizer> {

        T build(Map<String, Entity> context);

        Type<T> type();
    }
}
