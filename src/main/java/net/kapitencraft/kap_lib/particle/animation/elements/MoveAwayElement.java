package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class MoveAwayElement implements AnimationElement {
    private final PositionTarget target;
    private final float speed;
    private final int duration;

    public MoveAwayElement(PositionTarget target, float speed, int tickLength) {
        this.target = target;
        this.speed = speed;
        this.duration = tickLength;
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.MOVE_AWAY.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return duration;
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
                ByteBufCodecs.INT, e -> e.duration,
                MoveAwayElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MoveAwayElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<MoveAwayElement.Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder<MoveAwayElement> {
        private static final MapCodec<MoveAwayElement.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("target").forGetter(e -> e.target),
                Codec.FLOAT.fieldOf("speed").forGetter(e -> e.speed),
                Codec.INT.fieldOf("duration").forGetter(e -> e.duration)
        ).apply(i, MoveAwayElement.Builder::fromCodec));

        private static Builder fromCodec(PositionTarget.Builder<?> builder, Float speed, Integer time) {
            return new Builder().target(builder).speed(speed).time(time);
        }

        private PositionTarget.Builder<?> target;
        private float speed;
        private int duration;

        public Builder target(PositionTarget.Builder<?> target) {
            this.target = target;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder time(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public MoveAwayElement build(ParticleAnimationPresetContext context) {
            if (duration < 1) throw new IllegalStateException("time must be > 0");
            return new MoveAwayElement(target.build(context), speed, duration);
        }

        @Override
        public AnimationElement.Type<MoveAwayElement> type() {
            return null;
        }
    }
}
