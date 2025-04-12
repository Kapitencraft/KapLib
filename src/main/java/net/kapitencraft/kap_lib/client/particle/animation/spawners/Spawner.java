package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Spawner {

    @ApiStatus.Internal
    @OnlyIn(Dist.CLIENT)
    static Spawner fromNw(FriendlyByteBuf buf) {
        Spawner.Type<?> type = buf.readRegistryIdUnsafe(ExtraRegistries.SPAWN_ELEMENT_TYPES);
        return type.fromNw(buf, Minecraft.getInstance().level);
    }

    @ApiStatus.Internal
    static <T extends Spawner> void toNw(FriendlyByteBuf buf, T element) {
        VisibleSpawner.Type<T> type = Objects.requireNonNull((VisibleSpawner.Type<T>) element.getType(), "element " + element + " does not declare type!");
        buf.writeRegistryIdUnsafe(ExtraRegistries.SPAWN_ELEMENT_TYPES, type);
        type.toNW(buf, element);
    }

    /**
     * ticks this spawner. spawn Particles using {@link ParticleSpawnSink#accept(ParticleOptions, Vec3) ParticleSpawnSink#accept(...)}
     * @param sink the particle spawn acceptor
     */
    void spawn(ParticleSpawnSink sink);

    /**
     * @return the type of this spawner. must be registered to the {@link net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys#SPAWNER_TYPES ExtraRegistryKeys#SPAWN_ELEMENT_TYPES}
     */
    @NotNull VisibleSpawner.Type<? extends Spawner> getType();

    /**
     * the type of the spawner
     */
    interface Type<T extends Spawner> {
        /**
         * use {@link net.kapitencraft.kap_lib.helpers.NetworkHelper#writeParticleOptions(FriendlyByteBuf, ParticleOptions) NetworkHelper#writeParticleOptions} for encrypting the particle options
         * @param buf the target network buffer
         * @param value the Spawner to encrypt
         */
        void toNW(FriendlyByteBuf buf, T value);

        /**
         * @param buf the ByteBuf to read the information from
         * @param level the level to read extra information from (like Entities)
         * @return a generated spawner from the ByteBuf
         */
        @OnlyIn(Dist.CLIENT)
        T fromNw(FriendlyByteBuf buf, ClientLevel level);
    }

    interface Builder {
        Spawner build();
    }
}
