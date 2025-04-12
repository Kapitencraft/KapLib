package net.kapitencraft.kap_lib.client.util.rot_target;

import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;

/**
 * a rotation target that tracks a position from a source position (like Aim-Bot)
 */
public class TrackPositionRotationTarget implements RotationTarget {
    private final PositionTarget source, target;

    TrackPositionRotationTarget(PositionTarget source, PositionTarget target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Vec2 get() {
        return MathHelper.createTargetRotationFromPos(source.get(), target.get());
    }

    @Override
    public Types getType() {
        return Types.TRACK_POSITION;
    }

    public static class Type implements RotationTarget.Type<TrackPositionRotationTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, TrackPositionRotationTarget val) {
            val.source.toNw(buf);
            val.target.toNw(buf);
        }

        @Override
        public TrackPositionRotationTarget fromNw(FriendlyByteBuf buf) {
            return new TrackPositionRotationTarget(PositionTarget.fromNw(buf), PositionTarget.fromNw(buf));
        }
    }
}
