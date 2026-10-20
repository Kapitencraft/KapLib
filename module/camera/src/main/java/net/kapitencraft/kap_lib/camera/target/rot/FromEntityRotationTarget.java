package net.kapitencraft.kap_lib.camera.target.rot;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

import java.util.UUID;

/**
 * provides the position
 */
public class FromEntityRotationTarget implements RotationTarget {
    private final UUID entityId;

    FromEntityRotationTarget(UUID entityId) {
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
        private static final StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(FromEntityRotationTarget::new, t -> t.entityId);

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
        private static final MapCodec<Builder> CODEC = UUIDUtil.CODEC.xmap(a -> new Builder().setAccessor(a), t -> t.accessor).fieldOf("target");
        private UUID accessor;

        public Builder setAccessor(UUID accessor) {
            this.accessor = accessor;
            return this;
        }

        @Override
        public FromEntityRotationTarget build() {
            return new FromEntityRotationTarget(accessor);
        }

        @Override
        public Types type() {
            return Types.FROM_ENTITY;
        }
    }
}
