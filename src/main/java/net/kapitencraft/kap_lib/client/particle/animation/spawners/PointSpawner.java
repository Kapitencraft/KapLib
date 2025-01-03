package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * spawns particles exactly at the given point
 */
public class PointSpawner extends Spawner {
    private final Vec3 pos;

    public PointSpawner(ParticleOptions particle, Vec3 pos) {
        super(particle);
        this.pos = pos;
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        sink.accept(particle, pos);
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.POINT_SPAWN.get();
    }

    public static class Type implements Spawner.Type<PointSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, PointSpawner value) {
            NetworkHelper.writeParticleOptions(buf, value.particle);
            NetworkHelper.writeVec3(buf, value.pos);
        }

        @Override
        public PointSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new PointSpawner(NetworkHelper.readParticleOptions(buf), NetworkHelper.readVec3(buf));
        }
    }

    public static class Builder extends Spawner.Builder<Builder> {
        private Vec3 pos;

        public Builder pos(Vec3 pos) {
            this.pos = pos;
            return this;
        }

        @Override
        public Spawner build() {
            return new PointSpawner(particle, pos);
        }
    }
}
