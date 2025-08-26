package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class EntityPositionTarget implements PositionTarget {
    private final int target;
    private final EntityAnchorArgument.Anchor anchor;

    public EntityPositionTarget(int target, EntityAnchorArgument.Anchor anchor) {
        this.target = target;
        this.anchor = anchor;
    }

    @Override
    public Vec3 get() {
        return anchor.apply(ClientHelper.getEntity(target));
    }

    @Override
    public Types getType() {
        return Types.ENTITY;
    }

    public static class Type implements PositionTarget.Type<EntityPositionTarget> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityPositionTarget val) {
            buf.writeInt(val.target);
            buf.writeEnum(val.anchor);
        }

        @Override
        public EntityPositionTarget fromNw(FriendlyByteBuf buf) {
            return new EntityPositionTarget(buf.readInt(), buf.readEnum(EntityAnchorArgument.Anchor.class));
        }
    }

    @Override
    public String toString() {
        return "EntityPositionTarget[" + target + ']';
    }
}
