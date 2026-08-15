package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleData;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class StartFadeOutElement implements AnimationElement {
    private final float rate;

    public StartFadeOutElement(float rate) {
        this.rate = rate;
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.START_FADE_OUT.get();
    }

    @Override
    public int createLength(ParticleData config) {
        return 0;
    }

    @Override
    public void tick(ParticleData object, int tick, double percentage) {
        object.registerTicker((config, tickIndex) -> config.a += rate);
    }

    public static class Type implements AnimationElement.Type<StartFadeOutElement> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, StartFadeOutElement> STREAM_CODEC = ByteBufCodecs.FLOAT.map(StartFadeOutElement::new, e -> e.rate);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StartFadeOutElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<StartFadeOutElement.Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder<StartFadeOutElement> {
        private static final MapCodec<StartFadeOutElement.Builder> CODEC = Codec.FLOAT.xmap(f -> new Builder().rate(f), e -> e.rate).fieldOf("rate");

        private float rate;

        public Builder rate(float rate) {
            this.rate = rate;
            return this;
        }

        @Override
        public StartFadeOutElement build(ParticleAnimationPresetContext context) {
            return new StartFadeOutElement(rate);
        }

        @Override
        public AnimationElement.Type<StartFadeOutElement> type() {
            return ElementTypes.START_FADE_OUT.get();
        }
    }
}
