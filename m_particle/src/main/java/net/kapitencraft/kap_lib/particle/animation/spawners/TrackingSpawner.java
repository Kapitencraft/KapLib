package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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


        private static final StreamCodec<? super RegistryFriendlyByteBuf, TrackingSpawner> STREAM_CODEC = StreamCodec.composite(
                ParticleTypes.STREAM_CODEC, s -> s.particle,
                PositionTarget.STREAM_CODEC, s -> s.target,
                TrackingSpawner::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, TrackingSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder extends VisibleSpawner.Builder<Builder, TrackingSpawner> {
        private static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(s -> s.particle),
                PositionTarget.CODEC.fieldOf("position").forGetter(s -> s.target)
        ).apply(i, Builder::fromCodec));

        private static Builder fromCodec(ParticleOptions options, PositionTarget.Builder<?> positionTarget) {
            return new Builder().setParticle(options).target(positionTarget);
        }


        private PositionTarget.Builder<?> target;

        public Builder target(PositionTarget.Builder<?> target) {
            this.target = target;
            return this;
        }

        @Override
        public TrackingSpawner build(ParticleAnimationPresetContext context) {
            return new TrackingSpawner(particle, target.build(context));
        }

        @Override
        public Spawner.Type<TrackingSpawner> type() {
            return SpawnerTypes.TRACKING.get();
        }
    }
}
