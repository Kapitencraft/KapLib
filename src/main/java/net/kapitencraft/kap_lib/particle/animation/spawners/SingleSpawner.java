package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SingleSpawner extends VisibleSpawner {
    private final PositionTarget positionTarget;

    protected SingleSpawner(ParticleOptions particle, PositionTarget positionTarget) {
        super(particle);
        this.positionTarget = positionTarget;
    }

    public static Spawner.Builder at(ParticleOptions options, PositionTarget fixed) {
        return () -> new SingleSpawner(options, fixed);
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
        private static final MapCodec<SingleSpawner> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(s -> s.particle),
                PositionTarget.CODEC.fieldOf("position").forGetter(s -> s.positionTarget)
        ).apply(i, SingleSpawner::new));

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
        public MapCodec<SingleSpawner> codec() {
            return CODEC;
        }
    }
}
