package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record RelativePositionTarget(PositionTarget target, Vec3 offset) implements PositionTarget {

    @Override
    public Vec3 get() {
        return target.get().add(offset);
    }

    @Override
    public Types getType() {
        return Types.RELATIVE;
    }

    public static class Type implements PositionTarget.Type<RelativePositionTarget> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, RelativePositionTarget> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, RelativePositionTarget::target,
                ExtraStreamCodecs.VEC_3, RelativePositionTarget::get,
                RelativePositionTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RelativePositionTarget> codec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public String toString() {
        return "RelativePositionTarget[" + target + "], offset=" + this.offset;
    }
}
