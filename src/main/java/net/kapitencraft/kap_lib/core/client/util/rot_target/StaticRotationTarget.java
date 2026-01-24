package net.kapitencraft.kap_lib.core.client.util.rot_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

/**
 * provides a static rotation
 */
public class StaticRotationTarget implements RotationTarget {
    private final Vec2 rot;

    StaticRotationTarget(Vec2 rot) {
        this.rot = rot;
    }

    private StaticRotationTarget(float x, float y) {
        this(new Vec2(x, y));
    }

    @Override
    public Vec2 get() {
        return rot;
    }

    @Override
    public Types getType() {
        return Types.ABSOLUTE;
    }

    public static class Type implements RotationTarget.Type<StaticRotationTarget> {
        private static final MapCodec<StaticRotationTarget> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.FLOAT.fieldOf("x").forGetter(t -> t.rot.x),
                Codec.FLOAT.fieldOf("y").forGetter(t -> t.rot.y)
        ).apply(i, StaticRotationTarget::new));
        private static final StreamCodec<? super RegistryFriendlyByteBuf, StaticRotationTarget> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, t -> t.rot.x,
                ByteBufCodecs.FLOAT, t -> t.rot.y,
                StaticRotationTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StaticRotationTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<StaticRotationTarget> codec() {
            return CODEC;
        }
    }
}
