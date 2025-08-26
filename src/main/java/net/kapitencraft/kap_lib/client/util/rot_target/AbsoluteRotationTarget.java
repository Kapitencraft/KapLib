package net.kapitencraft.kap_lib.client.util.rot_target;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;

public class AbsoluteRotationTarget implements RotationTarget {
    private final Vec2 rot;

    AbsoluteRotationTarget(Vec2 rot) {
        this.rot = rot;
    }

    @Override
    public Vec2 get() {
        return rot;
    }

    @Override
    public Types getType() {
        return Types.ABSOLUTE;
    }

    public static class Type implements RotationTarget.Type<AbsoluteRotationTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, AbsoluteRotationTarget val) {
            buf.writeFloat(val.rot.x);
            buf.writeFloat(val.rot.y);
        }

        @Override
        public AbsoluteRotationTarget fromNw(FriendlyByteBuf buf) {
            return new AbsoluteRotationTarget(new Vec2(buf.readFloat(), buf.readFloat()));
        }
    }
}
