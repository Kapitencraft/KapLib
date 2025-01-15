package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
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
    public void tick(ParticleConfig object, int tick) {
        Vec3 relative = object.pos().subtract(target.get());
        object.setPos(
                object.pos().add(MathHelper.clampLength(relative, relative.length() + speed))
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Type implements AnimationElement.Type<MoveAwayElement> {

        @Override
        public MoveAwayElement fromNW(FriendlyByteBuf buf) {
            return new MoveAwayElement(PositionTarget.fromNw(buf), buf.readFloat(), buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, MoveAwayElement value) {
            value.target.toNw(buf);
            buf.writeFloat(value.speed);
            buf.writeInt(value.tickLength);
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
