package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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

    public static class Builder extends VisibleSpawner.Builder<Builder, LineSpawner> {
        private static final MapCodec<LineSpawner.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(s -> s.particle),
                PositionTarget.CODEC.fieldOf("start").forGetter(s -> s.start),
                PositionTarget.CODEC.fieldOf("end").forGetter(s -> s.end),
                Codec.FLOAT.fieldOf("spacing").forGetter(s -> s.spacing)
        ).apply(i, LineSpawner.Builder::fromCodec));

        private static Builder fromCodec(ParticleOptions options, PositionTarget start, PositionTarget end, Float spacing) {
            return new Builder().setParticle(options).start(start).end(end).spacing(spacing);
        }

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
        public LineSpawner build(Map<String, Entity> context) {
            return new LineSpawner(particle, start, end, spacing);
        }

        @Override
        public Spawner.Type<LineSpawner> type() {
            return SpawnerTypes.LINE.get();
        }
    }

    public static class Type implements VisibleSpawner.Type<LineSpawner> {

        private static final StreamCodec<? super RegistryFriendlyByteBuf, LineSpawner> STREAM_CODEC = StreamCodec.composite(
                ParticleTypes.STREAM_CODEC, s -> s.particle,
                PositionTarget.STREAM_CODEC, s -> s.start,
                PositionTarget.STREAM_CODEC, s -> s.end,
                ByteBufCodecs.FLOAT, s -> s.spacing,
                LineSpawner::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, LineSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> getCodec() {
            return Builder.CODEC;
        }
    }

    @Override
    public String toString() {
        return "LineSpawner from " + start + " to " + end + ", spacing = " + spacing + ", particle = " + particle;
    }
}
