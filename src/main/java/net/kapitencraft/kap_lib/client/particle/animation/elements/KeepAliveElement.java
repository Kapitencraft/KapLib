package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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
    public void tick(ParticleConfig object, int tick) {
        object.age = 0;
    }

    public static class Type implements AnimationElement.Type<KeepAliveElement> {

        @Override
        public KeepAliveElement fromNW(FriendlyByteBuf buf) {
            return new KeepAliveElement(buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, KeepAliveElement value) {
            buf.writeInt(value.duration);
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
