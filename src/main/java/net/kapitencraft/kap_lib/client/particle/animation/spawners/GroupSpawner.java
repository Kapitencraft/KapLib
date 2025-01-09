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

public class GroupSpawner extends Spawner {
    private final Spawner[] spawners;

    public GroupSpawner(ParticleOptions options, Spawner[] spawners) {
        super(options);
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

    public static class Builder extends Spawner.Builder<Builder> {
        private final List<Spawner> spawners = new ArrayList<>();

        public Builder addSpawner(Spawner.Builder<?> spawner) {
            spawners.add(spawner.build());
            return this;
        }

        //TODO extract particle cuz not necessary
        @Override
        public Spawner build() {
            return new GroupSpawner(particle, spawners.toArray(Spawner[]::new));
        }
    }

    public static class Type implements Spawner.Type<GroupSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, GroupSpawner value) {
            NetworkHelper.writeParticleOptions(buf, value.particle);
            NetworkHelper.writeArray(buf, value.spawners, Spawner::toNw);
        }

        @Override
        public GroupSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new GroupSpawner(NetworkHelper.readParticleOptions(buf), NetworkHelper.readArray(buf, Spawner[]::new, Spawner::fromNw));
        }
    }
}
