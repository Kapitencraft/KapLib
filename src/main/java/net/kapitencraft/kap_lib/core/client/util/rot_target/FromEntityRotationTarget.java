package net.kapitencraft.kap_lib.core.client.util.rot_target;

import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

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
        public StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> codec() {
            return STREAM_CODEC;
        }
    }
}
