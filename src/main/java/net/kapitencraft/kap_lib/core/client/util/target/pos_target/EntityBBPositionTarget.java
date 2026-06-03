package net.kapitencraft.kap_lib.core.client.util.target.pos_target;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.core.client.util.target.EntityAccessor;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

/**
 * gets a random position in the target entities bounding box
 */
public class EntityBBPositionTarget implements PositionTarget {
    private final UUID entity;

    public EntityBBPositionTarget(UUID entity) {
        this.entity = entity;
    }

    public static PositionTarget.Builder<EntityBBPositionTarget> builder(EntityAccessor accessor) {
        return new Builder(accessor);
    }

    @Override
    public Vec3 get() {
        return MathHelper.randomIn(MathHelper.RANDOM_SOURCE, ClientHelper.getEntity(entity).getBoundingBox());
    }

    @Override
    public Types getType() {
        return Types.ENTITY_BB;
    }

    public static class Type implements PositionTarget.Type<EntityBBPositionTarget> {
        private static final MapCodec<EntityBBPositionTarget.Builder> CODEC = EntityAccessor.CODEC.xmap(Builder::new, b -> b.owner).fieldOf("owner");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityBBPositionTarget> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityBBPositionTarget::new, t -> t.entity);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityBBPositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<EntityBBPositionTarget.Builder> codec() {
            return CODEC;
        }
    }

    public static class Builder implements PositionTarget.Builder<EntityBBPositionTarget> {

        private final EntityAccessor owner;

        public Builder(EntityAccessor owner) {
            this.owner = owner;
        }

        @Override
        public EntityBBPositionTarget build(Map<String, UUID> context) {
            return new EntityBBPositionTarget(this.owner.get(context));
        }

        @Override
        public Types getType() {
            return Types.ENTITY_BB;
        }
    }
}
