package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LineSpawner extends VisibleSpawner {
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
        List<Vec3> positions = MathHelper.makeLine(start.get(), end.get(), spacing);
        positions.forEach(v -> sink.accept(particle, v));
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.LINE.get();
    }

    public static class Builder extends VisibleSpawner.Builder<Builder> {
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
        public VisibleSpawner build() {
            return new LineSpawner(particle, start, end, spacing);
        }
    }

    public static class Type implements VisibleSpawner.Type<LineSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, LineSpawner value) {
            ExtraStreamCodecs.writeParticleOptions(buf, value.particle);
            value.start.toNw(buf);
            value.end.toNw(buf);
            buf.writeFloat(value.spacing);
        }

        @Override
        public LineSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new LineSpawner(ExtraStreamCodecs.readParticleOptions(buf), PositionTarget.fromNw(buf), PositionTarget.fromNw(buf), buf.readFloat());
        }
    }

    @Override
    public String toString() {
        return "LineSpawner from " + start + " to " + end + ", spacing = " + spacing;
    }
}
