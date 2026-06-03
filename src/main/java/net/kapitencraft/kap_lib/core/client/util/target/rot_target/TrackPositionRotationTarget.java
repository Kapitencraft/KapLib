package net.kapitencraft.kap_lib.core.client.util.target.rot_target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.target.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

import java.util.Map;
import java.util.UUID;

/**
 * a rotation target that tracks a position from a source position (like Aim-Bot)
 */
public record TrackPositionRotationTarget(PositionTarget source, PositionTarget target) implements RotationTarget {
    @Override
    public Vec2 get() {
        return MathHelper.createTargetRotationFromPos(source.get(), target.get());
    }

    @Override
    public Types getType() {
        return Types.TRACK_POSITION;
    }

    public static class Type implements RotationTarget.Type<TrackPositionRotationTarget> {

        private static final StreamCodec<? super RegistryFriendlyByteBuf, TrackPositionRotationTarget> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, TrackPositionRotationTarget::source,
                PositionTarget.STREAM_CODEC, TrackPositionRotationTarget::target,
                TrackPositionRotationTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, TrackPositionRotationTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements RotationTarget.Builder<TrackPositionRotationTarget> {
        private static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("source").forGetter(b -> b.source),
                PositionTarget.CODEC.fieldOf("target").forGetter(b -> b.target)
        ).apply(i, Builder::fromCodec));

        private static Builder fromCodec(PositionTarget.Builder<?> source, PositionTarget.Builder<?> target) {
            return new Builder().setSource(source).setTarget(target);
        }


        PositionTarget.Builder<?> source, target;

        public Builder setSource(PositionTarget.Builder<?> source) {
            this.source = source;
            return this;
        }

        public Builder setTarget(PositionTarget.Builder<?> target) {
            this.target = target;
            return this;
        }

        @Override
        public TrackPositionRotationTarget build(Map<String, UUID> context) {
            return new TrackPositionRotationTarget(source.build(context), target.build(context));
        }

        @Override
        public Types type() {
            return Types.TRACK_POSITION;
        }
    }
}
