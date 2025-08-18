package net.kapitencraft.kap_lib.client.util.pos_target;

import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record AbsolutePositionTarget(Vec3 get) implements PositionTarget {

    @Override
    public Types getType() {
        return Types.POS;
    }

    public static class Type implements PositionTarget.Type<AbsolutePositionTarget> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, AbsolutePositionTarget> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(AbsolutePositionTarget::new, AbsolutePositionTarget::get);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, AbsolutePositionTarget> codec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public String toString() {
        return "AbsolutePositionTarget@" + get;
    }
}
