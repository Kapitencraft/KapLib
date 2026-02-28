package net.kapitencraft.kap_lib.particle.animation.spawners;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface Spawner {
    StreamCodec<RegistryFriendlyByteBuf, Spawner> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.SPAWNER_TYPES).dispatch(Spawner::getType, Type::codec);

    /**
     * ticks this spawner. spawn Particles using {@link ParticleSpawnSink#accept(ParticleOptions, Vec3) ParticleSpawnSink#accept(...)}
     *
     * @param sink the particle spawn acceptor
     */
    void spawn(ParticleSpawnSink sink);

    /**
     * @return the type of this spawner. must be registered to the {@link ParticleAnimationRegistries.Keys#SPAWNER_TYPES ExtraRegistries.Keys#SPAWN_ELEMENT_TYPES}
     */
    @NotNull VisibleSpawner.Type<? extends Spawner> getType();

    /**
     * the type of the spawner containing the serializer
     * @param <T> the class type of the spawner
     */
    interface Type<T extends Spawner> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }

    interface Builder {
        Spawner build();
    }
}
