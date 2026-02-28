package net.kapitencraft.kap_lib.core.client.util.rot_target;

import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

/**
 * a rotation target that tracks a position from a source position (like Aim-Bot)
 * @param source the source to track the rotation from
 * @param target the target to track the rotation to
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
        public StreamCodec<? super RegistryFriendlyByteBuf, TrackPositionRotationTarget> codec() {
            return STREAM_CODEC;
        }
    }
}
