package net.kapitencraft.kap_lib.core.client.util.pos_target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

/**
 * a position target that has a static offset
 * @param target the target to be offset
 * @param offset the offset
 */
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
        private static final MapCodec<RelativePositionTarget> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("target").forGetter(RelativePositionTarget::target),
                Vec3.CODEC.fieldOf("offset").forGetter(RelativePositionTarget::offset)
        ).apply(i, RelativePositionTarget::new));
        private static final StreamCodec<? super RegistryFriendlyByteBuf, RelativePositionTarget> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, RelativePositionTarget::target,
                ExtraStreamCodecs.VEC_3, RelativePositionTarget::offset,
                RelativePositionTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RelativePositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<RelativePositionTarget> codec() {
            return CODEC;
        }
    }

    @Override
    public String toString() {
        return "RelativePositionTarget[" + target + "], offset=" + this.offset;
    }
}
