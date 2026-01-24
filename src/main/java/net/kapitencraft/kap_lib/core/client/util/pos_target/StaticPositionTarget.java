package net.kapitencraft.kap_lib.core.client.util.pos_target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

/**
 * a position target that always returns the same location (hence being static)
 * @param get the position of the target
 */
public record StaticPositionTarget(Vec3 get) implements PositionTarget {

    @Override
    public Types getType() {
        return Types.POS;
    }

    public static class Type implements PositionTarget.Type<StaticPositionTarget> {
        private static final MapCodec<StaticPositionTarget> CODEC = Vec3.CODEC.xmap(StaticPositionTarget::new, StaticPositionTarget::get).fieldOf("position");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, StaticPositionTarget> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(StaticPositionTarget::new, StaticPositionTarget::get);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StaticPositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<StaticPositionTarget> codec() {
            return CODEC;
        }
    }

    @Override
    public String toString() {
        return "StaticPositionTarget@" + get;
    }
}
