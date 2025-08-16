package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ParticleFinalizer {
    StreamCodec<RegistryFriendlyByteBuf, ParticleFinalizer> CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.FINALIZER_TYPES).dispatch(ParticleFinalizer::getType, Type::codec);

    @NotNull Type<? extends ParticleFinalizer> getType();

    void finalize(ParticleConfig config);

    interface Type<T extends ParticleFinalizer> {
        StreamCodec<RegistryFriendlyByteBuf, T> codec();
    }

    interface Builder {

        ParticleFinalizer build();
    }
}
