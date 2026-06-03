package net.kapitencraft.kap_lib.core.client.util.target.pos_target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

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
        public MapCodec<RelativePositionTarget.Builder> codec() {
            return Builder.CODEC;
        }
    }

    @Override
    public String toString() {
        return "RelativePositionTarget[" + target + "], offset=" + this.offset;
    }

    public static class Builder implements PositionTarget.Builder<RelativePositionTarget> {
        private static final MapCodec<RelativePositionTarget.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("target").forGetter(b -> b.target),
                Vec3.CODEC.fieldOf("offset").forGetter(b -> b.offset)
        ).apply(i, RelativePositionTarget.Builder::fromCodec));

        private static Builder fromCodec(PositionTarget.Builder<?> builder, Vec3 vec3) {
            return new Builder().setTarget(builder).setOffset(vec3);
        }


        private PositionTarget.Builder<?> target;
        private Vec3 offset;

        public Builder setTarget(PositionTarget.Builder<?> target) {
            this.target = target;
            return this;
        }

        public Builder setOffset(Vec3 offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public RelativePositionTarget build(Map<String, UUID> context) {
            return new RelativePositionTarget(target.build(context), offset);
        }

        @Override
        public Types getType() {
            return Types.RELATIVE;
        }
    }
}
