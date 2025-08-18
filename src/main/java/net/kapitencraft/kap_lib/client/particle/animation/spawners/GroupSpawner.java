package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupSpawner implements Spawner {
    private final List<Spawner> spawners;

    public GroupSpawner(List<Spawner> spawners) {
        this.spawners = spawners;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        for (Spawner spawner : spawners) spawner.spawn(sink);
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.GROUP.get();
    }

    public static class Builder implements Spawner.Builder {
        private final List<Spawner> spawners = new ArrayList<>();

        public Builder addSpawner(Spawner.Builder spawner) {
            spawners.add(spawner.build());
            return this;
        }

        @Override
        public Spawner build() {
            return new GroupSpawner(spawners);
        }
        }

    public static class Type implements VisibleSpawner.Type<GroupSpawner> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, GroupSpawner> STREAM_CODEC = Spawner.CODEC.apply(ByteBufCodecs.list()).map(GroupSpawner::new, s -> s.spawners);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GroupSpawner> codec() {
            return STREAM_CODEC;
        }
    }
}
