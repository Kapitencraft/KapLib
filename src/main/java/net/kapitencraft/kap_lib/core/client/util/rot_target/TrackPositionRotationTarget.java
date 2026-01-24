package net.kapitencraft.kap_lib.core.client.util.rot_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

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
        private static final MapCodec<TrackPositionRotationTarget> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("source").forGetter(TrackPositionRotationTarget::source),
                PositionTarget.CODEC.fieldOf("target").forGetter(TrackPositionRotationTarget::target)
        ).apply(i, TrackPositionRotationTarget::new));

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
        public MapCodec<TrackPositionRotationTarget> codec() {
            return CODEC;
        }
    }
}
