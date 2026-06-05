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

import java.util.Map;
import java.util.UUID;

public class SingleSpawner extends VisibleSpawner {
    private final PositionTarget positionTarget;

    protected SingleSpawner(ParticleOptions particle, PositionTarget positionTarget) {
        super(particle);
        this.positionTarget = positionTarget;
    }

    public static SpawnerBuilder<SingleSpawner> at(ParticleOptions options, PositionTarget.Builder<?> fixed) {
        return new SingleSpawner.Builder().setParticle(options).setPos(fixed);
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        sink.accept(this.particle, this.positionTarget.get());
    }

    @Override
    public @NotNull VisibleSpawner.Type<? extends Spawner> getType() {
        return SpawnerTypes.SINGLE.get();
    }

    public static class Type implements VisibleSpawner.Type<SingleSpawner> {

        private static final StreamCodec<RegistryFriendlyByteBuf, SingleSpawner> STREAM_CODEC = StreamCodec.composite(
                ParticleTypes.STREAM_CODEC, s -> s.particle,
                PositionTarget.STREAM_CODEC, s -> s.positionTarget,
                SingleSpawner::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SingleSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<SingleSpawner.Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder extends VisibleSpawner.Builder<Builder, SingleSpawner> {
        private static final MapCodec<SingleSpawner.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(s -> s.particle),
                PositionTarget.CODEC.fieldOf("position").forGetter(s -> s.builder)
        ).apply(i, SingleSpawner.Builder::fromCodec));

        private static Builder fromCodec(ParticleOptions options, PositionTarget.Builder<?> builder) {
            return new Builder().setParticle(options).setPos(builder);
        }

        private PositionTarget.Builder<?> builder;

        public Builder setPos(PositionTarget.Builder<?> builder) {
            this.builder = builder;
            return this;
        }

        @Override
        public SingleSpawner build(ParticleAnimationPresetContext context) {
            return new SingleSpawner(this.particle, this.builder.build(context));
        }

        @Override
        public Spawner.Type<SingleSpawner> type() {
            return null;
        }
    }
}
