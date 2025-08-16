package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record AbsolutePositionTarget(Vec3 get) implements PositionTarget {

    @Override
    public Types getType() {
        return Types.POS;
    }

    public static class Type implements PositionTarget.Type<AbsolutePositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, AbsolutePositionTarget val) {
            ExtraStreamCodecs.writeVec3(buf, val.get);
        }

        @Override
        public AbsolutePositionTarget fromNw(FriendlyByteBuf buf) {
            return new AbsolutePositionTarget(ExtraStreamCodecs.readVec3(buf));
        }
    }

    @Override
    public String toString() {
        return "AbsolutePositionTarget@" + get;
    }
}
