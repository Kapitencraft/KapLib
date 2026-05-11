package net.kapitencraft.kap_lib.particle.animation.store;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface EntityAccessor {
    Codec<EntityAccessor> CODEC = Codec.STRING.flatXmap(
            s -> DataResult.success(reference(s)),
            entityAccessor -> {
                if (entityAccessor instanceof Direct)
                    return DataResult.error(() -> "can not save direct entity accessor");
                return DataResult.success(((Reference) entityAccessor).name);
            });

    static EntityAccessor direct(Entity target) {
        return new Direct(target);
    }

    static EntityAccessor reference(String name) {
        return new Reference(name);
    }

    Entity get(Map<String, Entity> context);

    class Direct implements EntityAccessor {
        private final Entity entity;

        public Direct(Entity entity) {
            this.entity = entity;
        }

        @Override
        public Entity get(Map<String, Entity> context) {
            return entity;
        }
    }

    class Reference implements EntityAccessor {
        private final String name;

        public Reference(String name) {
            this.name = name;
        }

        @Override
        public Entity get(Map<String, Entity> context) {
            return context.get(name);
        }
    }
}
