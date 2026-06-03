package net.kapitencraft.kap_lib.core.client.util.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.UUID;

public interface EntityAccessor {
    Codec<EntityAccessor> CODEC = Codec.STRING.flatXmap(
            s -> DataResult.success(reference(s)),
            entityAccessor -> {
                if (entityAccessor instanceof Direct)
                    return DataResult.error(() -> "can not save direct entity accessor");
                return DataResult.success(((Reference) entityAccessor).name);
            });

    static EntityAccessor direct(Entity target) {
        return new Direct(target.getUUID());
    }

    static EntityAccessor reference(String name) {
        return new Reference(name);
    }

    UUID get(Map<String, UUID> context);

    record Direct(UUID entity) implements EntityAccessor {

        @Override
        public UUID get(Map<String, UUID> context) {
            return entity;
        }
    }

    record Reference(String name) implements EntityAccessor {

        @Override
        public UUID get(Map<String, UUID> context) {
            return context.get(name);
        }
    }
}
