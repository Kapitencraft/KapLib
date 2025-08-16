package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * spawns particles exactly at the given point
 */
public class TrackingSpawner extends VisibleSpawner {
    private final PositionTarget target;

    public TrackingSpawner(ParticleOptions particle, PositionTarget target) {
        super(particle);
        this.target = target;
    }

    @Override
    public String toString() {
        return "TrackingSpawner[" + target + "]";
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        sink.accept(particle, target.get());
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.TRACKING.get();
    }

    public static class Type implements VisibleSpawner.Type<TrackingSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, TrackingSpawner value) {
            ExtraStreamCodecs.writeParticleOptions(buf, value.particle);
            value.target.toNw(buf);
        }

        @Override
        public TrackingSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new TrackingSpawner(ExtraStreamCodecs.readParticleOptions(buf), PositionTarget.fromNw(buf));
        }
    }

    public static class Builder extends VisibleSpawner.Builder<Builder> {
        private PositionTarget target;

        public Builder target(PositionTarget target) {
            this.target = target;
            return this;
        }

        @Override
        public VisibleSpawner build() {
            return new TrackingSpawner(particle, target);
        }
    }
}
