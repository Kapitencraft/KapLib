package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * [WIP] does some weird shenanigans
 */
public class RotateElement implements AnimationElement {
    private final PositionTarget pivot;
    private final float degreePerTick;
    private final int duration;
    private final Direction.Axis axis;

    //TODO fix weird bug
    public RotateElement(PositionTarget pivot, float degreePerTick, int duration, Direction.Axis axis) {
        this.pivot = pivot;
        this.degreePerTick = degreePerTick;
        this.duration = duration;
        this.axis = axis;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull AnimationElement.Type<? extends AnimationElement> getType() {
        return ElementTypes.ROTATE.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        object.setPos(
                MathHelper.rotateAroundAxis(object.pos(), pivot.get(), degreePerTick, axis)
        );
    }

    public static class Builder implements AnimationElement.Builder {
        private PositionTarget pivot;
        private float angle;
        private int duration;
        private Direction.Axis axis;

        public Builder pivot(PositionTarget target) {
            this.pivot = target;
            return this;
        }

        public Builder axis(Direction.Axis axis) {
            this.axis = axis;
            return this;
        }

        public Builder angle(float angle) {
            this.angle = angle;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public AnimationElement build() {
            return new RotateElement(pivot, angle, duration, axis);
        }
    }

    public static class Type implements AnimationElement.Type<RotateElement> {

        @Override
        public RotateElement fromNW(FriendlyByteBuf buf) {
            PositionTarget pivot = PositionTarget.fromNw(buf);
            return new RotateElement(pivot, buf.readFloat(), buf.readInt(), buf.readEnum(Direction.Axis.class));
        }

        @Override
        public void toNW(FriendlyByteBuf buf, RotateElement value) {
            value.pivot.toNw(buf);
            buf.writeFloat(value.degreePerTick);
            buf.writeInt(value.duration);
            buf.writeEnum(value.axis);
        }
    }
}
