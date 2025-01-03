package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public interface ParticleFinalizer {

    static ParticleFinalizer fromNw(FriendlyByteBuf buf) {
        ParticleFinalizer.Type<?> type = buf.readRegistryIdUnsafe(ExtraRegistries.PARTICLE_FINALIZER_TYPES);
        return type.fromNw(buf, Minecraft.getInstance().level);
    }

    static <T extends ParticleFinalizer> void toNw(FriendlyByteBuf buf, T finalizer) {
        ParticleFinalizer.Type<T> type = (ParticleFinalizer.Type<T>) finalizer.getType();
        buf.writeRegistryIdUnsafe(ExtraRegistries.PARTICLE_FINALIZER_TYPES, type);
        type.toNw(buf, finalizer);
    }

    @NotNull Type<? extends ParticleFinalizer> getType();

    void finalize(ParticleConfig config);

    interface Type<T extends ParticleFinalizer> {

        void toNw(FriendlyByteBuf buf, T val);

        T fromNw(FriendlyByteBuf buf, ClientLevel level);
    }

    interface Builder {

        ParticleFinalizer build();
    }
}
