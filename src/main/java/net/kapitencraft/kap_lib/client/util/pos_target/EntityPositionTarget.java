package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, t -> t.target,
                ExtraStreamCodecs.enumCodec(EntityAnchorArgument.Anchor.values()), t -> t.anchor,
                EntityPositionTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> codec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public String toString() {
        return "EntityPositionTarget[" + target + ']';
    }
}
