package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SingleSpawner extends VisibleSpawner {
    private final PositionTarget positionTarget;

    protected SingleSpawner(String texture, PositionTarget positionTarget) {
        super(texture);
        this.positionTarget = positionTarget;
    }

    public static SpawnerBuilder<SingleSpawner> at(String texture, PositionTarget.Builder<?> fixed) {
        return new SingleSpawner.Builder().setTexture(texture).setPos(fixed);
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        sink.accept(this.positionTarget.get(), this.texture);
    }

    @Override
    public @NotNull VisibleSpawner.Type<? extends Spawner> getType() {
        return SpawnerTypes.SINGLE.get();
    }

    public static class Type implements VisibleSpawner.Type<SingleSpawner> {

        private static final StreamCodec<RegistryFriendlyByteBuf, SingleSpawner> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, s -> s.texture,
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
                Codec.STRING.fieldOf("particle").forGetter(s -> s.texture),
                PositionTarget.CODEC.fieldOf("position").forGetter(s -> s.builder)
        ).apply(i, SingleSpawner.Builder::fromCodec));

        private static Builder fromCodec(String texture, PositionTarget.Builder<?> builder) {
            return new Builder().setTexture(texture).setPos(builder);
        }

        private PositionTarget.Builder<?> builder;

        public Builder setPos(PositionTarget.Builder<?> builder) {
            this.builder = builder;
            return this;
        }

        @Override
        public SingleSpawner build(ParticleAnimationPresetContext context) {
            return new SingleSpawner(this.texture, this.builder.build(context));
        }

        @Override
        public Spawner.Type<SingleSpawner> type() {
            return null;
        }
    }
}
