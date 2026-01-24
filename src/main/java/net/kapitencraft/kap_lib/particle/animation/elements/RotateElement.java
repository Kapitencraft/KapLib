package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * rotates each applied particle around the provided pivot
 */
public class RotateElement implements AnimationElement {
    private final PositionTarget pivot;
    private final float degreePerTick;
    private final int duration;
    private final Direction.Axis axis;

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
        Vec3 pv = pivot.get();
        object.setPos(
                MathHelper.rotateAroundAxis(object.pos(), pv, degreePerTick, axis)
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
        private static final MapCodec<RotateElement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("pivot").forGetter(e -> e.pivot),
                Codec.FLOAT.fieldOf("speed").forGetter(e -> e.degreePerTick),
                Codec.INT.fieldOf("duration").forGetter(e -> e.duration),
                Direction.Axis.CODEC.fieldOf("axis").forGetter(e -> e.axis)
        ).apply(i, RotateElement::new));

        private static final StreamCodec<? super RegistryFriendlyByteBuf, RotateElement> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, e -> e.pivot,
                ByteBufCodecs.FLOAT, e -> e.degreePerTick,
                ByteBufCodecs.INT, e -> e.duration,
                ExtraStreamCodecs.enumCodec(Direction.Axis.values()), e -> e.axis,
                RotateElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RotateElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<RotateElement> codec() {
            return CODEC;
        }
    }
}
