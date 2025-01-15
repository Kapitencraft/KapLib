package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupSpawner implements Spawner {
    private final Spawner[] spawners;

    public GroupSpawner(Spawner[] spawners) {
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
            return new GroupSpawner(spawners.toArray(Spawner[]::new));
        }
    }

    public static class Type implements VisibleSpawner.Type<GroupSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, GroupSpawner value) {
            NetworkHelper.writeArray(buf, value.spawners, Spawner::toNw);
        }

        @Override
        public GroupSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new GroupSpawner(NetworkHelper.readArray(buf, Spawner[]::new, Spawner::fromNw));
        }
    }
}
