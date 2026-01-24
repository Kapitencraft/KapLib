package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
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
    public int createLength(ParticleConfig config) {
        return 0;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        object.registerTicker((config, tickIndex) -> config.a += rate);
    }

    public static class Type implements AnimationElement.Type<StartFadeOutElement> {
        private static final MapCodec<StartFadeOutElement> CODEC = Codec.FLOAT.xmap(StartFadeOutElement::new, e -> e.rate).fieldOf("rate");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, StartFadeOutElement> STREAM_CODEC = ByteBufCodecs.FLOAT.map(StartFadeOutElement::new, e -> e.rate);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StartFadeOutElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<StartFadeOutElement> codec() {
            return CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder {
        private float rate;

        public Builder rate(float rate) {
            this.rate = rate;
            return this;
        }

        @Override
        public AnimationElement build() {
            return new StartFadeOutElement(rate);
        }
    }
}
