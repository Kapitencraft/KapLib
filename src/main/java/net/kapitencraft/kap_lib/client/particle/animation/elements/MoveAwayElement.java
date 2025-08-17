package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MoveAwayElement implements AnimationElement {
    private final PositionTarget target;
    private final float speed;
    private final int tickLength;

    public MoveAwayElement(PositionTarget target, float speed, int tickLength) {
        this.target = target;
        this.speed = speed;
        this.tickLength = tickLength;
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.MOVE_AWAY.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return tickLength;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        Vec3 relative = object.pos().subtract(target.get());
        object.setPos(
                object.pos().add(MathHelper.clampLength(relative, relative.length() + speed))
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Type implements AnimationElement.Type<MoveAwayElement> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, MoveAwayElement> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, e -> e.target,
                ByteBufCodecs.FLOAT, e -> e.speed,
                ByteBufCodecs.INT, e -> e.tickLength,
                MoveAwayElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MoveAwayElement> codec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder {
        private PositionTarget target;
        private float speed;
        private int time;

        public Builder target(PositionTarget target) {
            this.target = target;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public AnimationElement build() {
            if (time < 1) throw new IllegalStateException("time must be > 0");
            return new MoveAwayElement(target, speed, time);
        }
    }
}
