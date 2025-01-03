package net.kapitencraft.kap_lib.client.particle.animation.util.pos_target;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EntityPositionTarget implements PositionTarget {
    private final Entity target;

    public EntityPositionTarget(Entity target) {
        this.target = target;
    }

    @Override
    public Vec3 pos() {
        return target.position();
    }

    @Override
    public Types getType() {
        return Types.ENTITY;
    }

    public static class Type implements PositionTarget.Type<EntityPositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityPositionTarget val) {
            buf.writeInt(val.target.getId());
        }

        @Override
        public EntityPositionTarget fromNw(FriendlyByteBuf buf) {
            return new EntityPositionTarget(Objects.requireNonNull(Minecraft.getInstance().level).getEntity(buf.readInt()));
        }
    }
}
