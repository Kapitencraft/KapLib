package net.kapitencraft.kap_lib.core.client.util.rot_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import org.checkerframework.checker.units.qual.C;

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
        private static final MapCodec<FromEntityRotationTarget> CODEC = Codec.INT.xmap(FromEntityRotationTarget::new, t -> t.entityId).fieldOf("target");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> STREAM_CODEC = ByteBufCodecs.INT.map(FromEntityRotationTarget::new, t -> t.entityId);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FromEntityRotationTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<FromEntityRotationTarget> codec() {
            return CODEC;
        }
    }
}
