package net.kapitencraft.kap_lib.particle.animation.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.minecraft.world.phys.Vec3;

public interface PositionAccessor {
    Codec<PositionAccessor> CODEC = Codec.STRING.flatXmap(
            s -> DataResult.success(reference(s)),
            entityAccessor -> {
                if (entityAccessor instanceof Direct)
                    return DataResult.error(() -> "can not save direct entity accessor");
                return DataResult.success(((Reference) entityAccessor).name);
            });

    static PositionAccessor reference(String name) {
        return new Reference(name);
    }

    static PositionAccessor direct(Vec3 pos) {
        return new Direct(pos);
    }

    Vec3 get(ParticleAnimationPresetContext context);

    class Direct implements PositionAccessor {
        private final Vec3 pos;

        public Direct(Vec3 pos) {
            this.pos = pos;
        }

        @Override
        public Vec3 get(ParticleAnimationPresetContext context) {
            return pos;
        }
    }

    class Reference implements PositionAccessor {
        private final String name;

        public Reference(String name) {
            this.name = name;
        }

        @Override
        public Vec3 get(ParticleAnimationPresetContext context) {
            return context.getPositionParam(name);
        }
    }
}
