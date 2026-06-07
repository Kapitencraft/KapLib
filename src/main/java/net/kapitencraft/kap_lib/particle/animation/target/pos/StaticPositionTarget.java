package net.kapitencraft.kap_lib.particle.animation.target.pos;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.PositionAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

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
        private static final StreamCodec<? super RegistryFriendlyByteBuf, StaticPositionTarget> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(StaticPositionTarget::new, StaticPositionTarget::get);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, StaticPositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<StaticPositionTarget.Builder> codec() {
            return Builder.CODEC;
        }
    }

    @Override
    public String toString() {
        return "StaticPositionTarget@" + get;
    }

    public static class Builder implements PositionTarget.Builder<StaticPositionTarget> {
        private static final MapCodec<StaticPositionTarget.Builder> CODEC = PositionAccessor.CODEC.xmap(b -> new Builder().setPos(b), b -> b.pos).fieldOf("position");
        private PositionAccessor pos;

        private Builder setPos(PositionAccessor accessor) {
            this.pos = accessor;
            return this;
        }

        public Builder setPos(Vec3 pos) {
            this.pos = PositionAccessor.direct(pos);
            return this;
        }

        public Builder setPos(String name) {
            this.pos = PositionAccessor.reference(name);
            return this;
        }

        @Override
        public StaticPositionTarget build(ParticleAnimationPresetContext context) {
            return new StaticPositionTarget(this.pos.get(context));
        }

        @Override
        public Types getType() {
            return Types.POS;
        }
    }
}
