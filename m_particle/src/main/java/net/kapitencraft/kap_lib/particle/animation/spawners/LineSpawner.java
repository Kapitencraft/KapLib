package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LineSpawner extends VisibleSpawner {
    private final PositionTarget start, end;
    private final float spacing;

    public LineSpawner(String texture, PositionTarget start, PositionTarget end, float spacing) {
        super(texture);
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
        positions.forEach(v -> sink.accept(v, texture));
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.LINE.get();
    }

    public static class Builder extends VisibleSpawner.Builder<Builder, LineSpawner> {
        private static final MapCodec<LineSpawner.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.fieldOf("texture").forGetter(s -> s.texture),
                PositionTarget.CODEC.fieldOf("start").forGetter(s -> s.start),
                PositionTarget.CODEC.fieldOf("end").forGetter(s -> s.end),
                Codec.FLOAT.fieldOf("spacing").forGetter(s -> s.spacing)
        ).apply(i, LineSpawner.Builder::fromCodec));

        private static Builder fromCodec(String texture, PositionTarget.Builder<?> start, PositionTarget.Builder<?> end, float spacing) {
            return new Builder().setTexture(texture).start(start).end(end).spacing(spacing);
        }

        private PositionTarget.Builder<?> start, end;
        private float spacing;

        public Builder start(PositionTarget.Builder<?> start) {
            this.start = start;
            return this;
        }

        public Builder end(PositionTarget.Builder<?> end) {
            this.end = end;
            return this;
        }

        public Builder spacing(float spacing) {
            this.spacing = spacing;
            return this;
        }

        @Override
        public LineSpawner build(ParticleAnimationPresetContext context) {
            return new LineSpawner(this.texture, start.build(context), end.build(context), spacing);
        }

        @Override
        public Spawner.Type<LineSpawner> type() {
            return SpawnerTypes.LINE.get();
        }
    }

    public static class Type implements VisibleSpawner.Type<LineSpawner> {

        private static final StreamCodec<? super RegistryFriendlyByteBuf, LineSpawner> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, s -> s.texture,
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
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    @Override
    public String toString() {
        return "LineSpawner from " + start + " to " + end + ", spacing = " + spacing + ", particle = " + texture;
    }
}
