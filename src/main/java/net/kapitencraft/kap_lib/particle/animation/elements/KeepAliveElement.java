package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class KeepAliveElement implements AnimationElement {
    private final int duration;

    public KeepAliveElement(int duration) {
        this.duration = duration;
    }

    public static Builder forDuration(int duration) {
        return new Builder().duration(duration);
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.KEEP_ALIVE.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        object.age = 0;
    }

    public static class Type implements AnimationElement.Type<KeepAliveElement> {
        private static final MapCodec<KeepAliveElement> CODEC = Codec.INT.xmap(KeepAliveElement::new, e -> e.duration).fieldOf("duration");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, KeepAliveElement> STREAM_CODEC = ByteBufCodecs.INT.map(KeepAliveElement::new, e -> e.duration);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, KeepAliveElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<KeepAliveElement> codec() {
            return CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder {
        private int duration;

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public AnimationElement build() {
            return new KeepAliveElement(duration);
        }
    }
}
