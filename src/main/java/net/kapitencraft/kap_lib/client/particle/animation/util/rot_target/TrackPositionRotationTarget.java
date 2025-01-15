package net.kapitencraft.kap_lib.client.particle.animation.util.rot_target;

import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;

/**
 * a rotation target that tracks a position from a source position (like Aim-Bot)
 */
public class TrackPositionRotationTarget implements RotationTarget {
    PositionTarget source, target;

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

        }

        @Override
        public TrackPositionRotationTarget fromNw(FriendlyByteBuf buf) {
            return null;
        }
    }
}
