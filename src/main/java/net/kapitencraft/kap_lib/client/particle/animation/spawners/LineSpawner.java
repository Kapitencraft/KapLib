package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class LineSpawner extends Spawner {
    private final PositionTarget start, end;
    private final float spacing;

    public LineSpawner(ParticleOptions options, PositionTarget start, PositionTarget end, float spacing) {
        super(options);
        this.start = start;
        this.end = end;
        this.spacing = spacing;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {

    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.LINE.get();
    }

    public static class Builder extends Spawner.Builder<Builder> {
        private PositionTarget start, end;
        private float spacing;

        public Builder start(PositionTarget start) {
            this.start = start;
            return this;
        }

        public Builder end(PositionTarget end) {
            this.end = end;
            return this;
        }

        public Builder spacing(float spacing) {
            this.spacing = spacing;
            return this;
        }

        @Override
        public Spawner build() {
            return new LineSpawner(particle, start, end, spacing);
        }
    }

    public static class Type implements Spawner.Type<LineSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, LineSpawner value) {
            NetworkHelper.writeParticleOptions(buf, value.particle);
            value.start.toNw(buf);
            value.end.toNw(buf);
            buf.writeFloat(value.spacing);
        }

        @Override
        public LineSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new LineSpawner(NetworkHelper.readParticleOptions(buf), PositionTarget.fromNw(buf), PositionTarget.fromNw(buf), buf.readFloat());
        }
    }
}
