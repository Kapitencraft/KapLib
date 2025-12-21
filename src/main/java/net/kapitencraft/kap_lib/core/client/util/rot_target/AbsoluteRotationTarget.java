package net.kapitencraft.kap_lib.core.client.util.rot_target;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

public class AbsoluteRotationTarget implements RotationTarget {
    private final Vec2 rot;

    AbsoluteRotationTarget(Vec2 rot) {
        this.rot = rot;
    }

    @Override
    public Vec2 get() {
        return rot;
    }

    @Override
    public Types getType() {
        return Types.ABSOLUTE;
    }

    public static class Type implements RotationTarget.Type<AbsoluteRotationTarget> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, AbsoluteRotationTarget> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, t -> t.rot.x,
                ByteBufCodecs.FLOAT, t -> t.rot.y,
                (x, y) -> new AbsoluteRotationTarget(new Vec2(x, y))
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, AbsoluteRotationTarget> codec() {
            return STREAM_CODEC;
        }
    }
}
