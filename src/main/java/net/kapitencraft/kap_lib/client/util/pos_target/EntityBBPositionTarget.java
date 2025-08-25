package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityBBPositionTarget implements PositionTarget {
    private final int entity;

    public EntityBBPositionTarget(int entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 get() {
        return MathHelper.randomIn(KapLibMod.RANDOM_SOURCE, ClientHelper.getEntity(entity).getBoundingBox());
    }

    @Override
    public Types getType() {
        return Types.ENTITY_BB;
    }

    public static class Type implements PositionTarget.Type<EntityBBPositionTarget> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityBBPositionTarget> STREAM_CODEC = ByteBufCodecs.INT.map(EntityBBPositionTarget::new, t -> t.entity);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityBBPositionTarget> codec() {
            return STREAM_CODEC;
        }
    }
}
