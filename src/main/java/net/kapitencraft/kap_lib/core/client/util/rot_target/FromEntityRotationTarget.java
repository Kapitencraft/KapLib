package net.kapitencraft.kap_lib.core.client.util.rot_target;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.particle.animation.store.EntityAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

import java.util.Map;

/**
 * provides the position
 */
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
        private static final StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> STREAM_CODEC = ByteBufCodecs.INT.map(FromEntityRotationTarget::new, t -> t.entityId);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements RotationTarget.Builder<FromEntityRotationTarget> {
        private static final MapCodec<Builder> CODEC = EntityAccessor.CODEC.xmap(a -> new Builder().setAccessor(a), t -> t.accessor).fieldOf("target");
        private EntityAccessor accessor;

        public Builder setAccessor(EntityAccessor accessor) {
            this.accessor = accessor;
            return this;
        }

        @Override
        public FromEntityRotationTarget build(Map<String, Entity> context) {
            return new FromEntityRotationTarget(accessor.get(context).getId());
        }

        @Override
        public Types type() {
            return Types.FROM_ENTITY;
        }
    }
}
