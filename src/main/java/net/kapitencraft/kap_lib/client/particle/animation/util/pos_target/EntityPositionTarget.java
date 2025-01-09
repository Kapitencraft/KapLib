package net.kapitencraft.kap_lib.client.particle.animation.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EntityPositionTarget implements PositionTarget {
    private final int target;

    public EntityPositionTarget(int target) {
        this.target = target;
    }

    @Override
    public Vec3 pos() {
        return ClientHelper.getEntity(target).position();
    }

    @Override
    public Types getType() {
        return Types.ENTITY;
    }

    public static class Type implements PositionTarget.Type<EntityPositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityPositionTarget val) {
            buf.writeInt(val.target);
        }

        @Override
        public EntityPositionTarget fromNw(FriendlyByteBuf buf) {
            return new EntityPositionTarget(buf.readInt());
        }
    }

    @Override
    public String toString() {
        return "EntityPositionTarget[" + target + ']';
    }
}
