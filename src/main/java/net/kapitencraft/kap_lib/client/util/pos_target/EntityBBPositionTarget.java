package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityBBPositionTarget implements PositionTarget {
    private final Entity entity;

    public EntityBBPositionTarget(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 get() {
        return MathHelper.randomIn(KapLibMod.RANDOM_SOURCE, entity.getBoundingBox());
    }

    @Override
    public Types getType() {
        return Types.ENTITY_BB;
    }

    public static class Type implements PositionTarget.Type<EntityBBPositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityBBPositionTarget val) {
            buf.writeInt(val.entity.getId());
        }

        @Override
        public EntityBBPositionTarget fromNw(FriendlyByteBuf buf) {
            return new EntityBBPositionTarget(ClientHelper.getEntity(buf.readInt()));
        }
    }
}
