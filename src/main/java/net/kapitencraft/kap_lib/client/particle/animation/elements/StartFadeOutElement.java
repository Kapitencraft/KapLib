package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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
    public void tick(ParticleConfig object, int tick) {
        object.registerTicker((config, tickIndex) -> config.a += rate);
    }

    public static class Type implements AnimationElement.Type<StartFadeOutElement> {

        @Override
        public StartFadeOutElement fromNW(FriendlyByteBuf buf) {
            return new StartFadeOutElement(buf.readFloat());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, StartFadeOutElement value) {

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
