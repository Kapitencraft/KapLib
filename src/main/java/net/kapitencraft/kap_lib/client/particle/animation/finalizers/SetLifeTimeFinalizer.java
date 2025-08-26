package net.kapitencraft.kap_lib.client.particle.animation.finalizers;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.FinalizerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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
    public void finalize(ParticleConfig config) {
        config.lifeTime = lifeTime;
        if (resetAge) config.age = 0;
    }

    public static class Type implements ParticleFinalizer.Type<SetLifeTimeFinalizer> {

        @Override
        public void toNw(FriendlyByteBuf buf, SetLifeTimeFinalizer val) {
            buf.writeInt(val.lifeTime);
            buf.writeBoolean(val.resetAge);
        }

        @Override
        public SetLifeTimeFinalizer fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new SetLifeTimeFinalizer(buf.readInt(), buf.readBoolean());
        }
    }

    public static class Builder implements ParticleFinalizer.Builder {
        private int lifeTime;
        private boolean resetAge = false;

        public Builder resetAge() {
            this.resetAge = true;
            return this;
        }

        public Builder lifeTime(int lifeTime) {
            this.lifeTime = lifeTime;
            return this;
        }

        @Override
        public ParticleFinalizer build() {
            return new SetLifeTimeFinalizer(lifeTime, resetAge);
        }
    }

    @Override
    public String toString() {
        return "SetLifeTimeFinalizer{" +
                "lifeTime=" + lifeTime + (resetAge ? ", resetsAge" : "") +
                '}';
    }
}
