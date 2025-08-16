package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class RelativePositionTarget implements PositionTarget {
    private final PositionTarget target;
    private final Vec3 offset;

    public RelativePositionTarget(PositionTarget target, Vec3 offset) {
        this.target = target;
        this.offset = offset;
    }

    @Override
    public Vec3 get() {
        return target.get().add(offset);
    }

    @Override
    public Types getType() {
        return Types.RELATIVE;
    }

    public static class Type implements PositionTarget.Type<RelativePositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, RelativePositionTarget val) {
            val.target.toNw(buf);
            ExtraStreamCodecs.writeVec3(buf, val.offset);
        }

        @Override
        public RelativePositionTarget fromNw(FriendlyByteBuf buf) {
            return new RelativePositionTarget(PositionTarget.fromNw(buf), ExtraStreamCodecs.readVec3(buf));
        }
    }

    @Override
    public String toString() {
        return "RelativePositionTarget[" + target + "], offset=" + this.offset;
    }
}
