package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleData;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.FinalizerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SetLifeTimeFinalizer implements ParticleFinalizer {
    private final int lifeTime;
    private final boolean resetAge;

    public SetLifeTimeFinalizer(int lifeTime, boolean resetAge) {
        this.lifeTime = lifeTime;
        this.resetAge = resetAge;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Type getType() {
        return FinalizerTypes.SET_LIFE_TIME.get();
    }

    @Override
    public void finalize(ParticleData config) {
        config.lifeTime = lifeTime;
        if (resetAge) config.age = 0;
    }

    public static class Type implements ParticleFinalizer.Type<SetLifeTimeFinalizer> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, SetLifeTimeFinalizer> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, f -> f.lifeTime,
                ByteBufCodecs.BOOL, f -> f.resetAge,
                SetLifeTimeFinalizer::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SetLifeTimeFinalizer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements ParticleFinalizer.Builder<SetLifeTimeFinalizer> {
        private static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("lifeTime").forGetter(f -> f.lifeTime),
                Codec.BOOL.optionalFieldOf("resetAge", false).forGetter(f -> f.resetAge)
        ).apply(i, Builder::fromCodec));

        private static Builder fromCodec(Integer integer, Boolean aBoolean) {
            return new Builder().lifeTime(integer).resetAge(aBoolean);
        }

        private int lifeTime;
        private boolean resetAge = false;

        public Builder resetAge() {
            this.resetAge = true;
            return this;
        }

        private Builder resetAge(boolean resetAge) {
            this.resetAge = resetAge;
            return this;
        }

        public Builder lifeTime(int lifeTime) {
            this.lifeTime = lifeTime;
            return this;
        }

        @Override
        public SetLifeTimeFinalizer build(ParticleAnimationPresetContext context) {
            return new SetLifeTimeFinalizer(lifeTime, resetAge);
        }

        @Override
        public ParticleFinalizer.Type<SetLifeTimeFinalizer> type() {
            return FinalizerTypes.SET_LIFE_TIME.get();
        }
    }

    @Override
    public String toString() {
        return "SetLifeTimeFinalizer{" +
                "lifeTime=" + lifeTime + (resetAge ? ", resetsAge" : "") +
                '}';
    }
}
