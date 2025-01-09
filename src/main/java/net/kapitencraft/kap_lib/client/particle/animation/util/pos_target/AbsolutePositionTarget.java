package net.kapitencraft.kap_lib.client.particle.animation.util.pos_target;

import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record AbsolutePositionTarget(Vec3 pos) implements PositionTarget {

    @Override
    public Types getType() {
        return Types.POS;
    }

    public static class Type implements PositionTarget.Type<AbsolutePositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, AbsolutePositionTarget val) {
            NetworkHelper.writeVec3(buf, val.pos);
        }

        @Override
        public AbsolutePositionTarget fromNw(FriendlyByteBuf buf) {
            return new AbsolutePositionTarget(NetworkHelper.readVec3(buf));
        }
    }

    @Override
    public String toString() {
        return "AbsolutePositionTarget@" + pos;
    }
}
