package net.kapitencraft.kap_lib.client.particle.animation.util.rot_target;

import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;

public class FromEntityRotationTarget implements RotationTarget {
    private final int entityId;

    FromEntityRotationTarget(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public Vec2 get() {
        return ClientHelper.getEntity(entityId).getRotationVector();
    }

    @Override
    public Types getType() {
        return Types.FROM_ENTITY;
    }

    public static class Type implements RotationTarget.Type<FromEntityRotationTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, FromEntityRotationTarget val) {
            buf.writeInt(val.entityId);
        }

        @Override
        public FromEntityRotationTarget fromNw(FriendlyByteBuf buf) {
            return new FromEntityRotationTarget(buf.readInt());
        }
    }
}
