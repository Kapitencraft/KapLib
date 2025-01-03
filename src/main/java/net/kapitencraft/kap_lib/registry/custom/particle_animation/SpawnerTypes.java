package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.EntityBBSpawner;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.PointSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface SpawnerTypes {

    DeferredRegister<Spawner.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.SPAWN_ELEMENT_TYPES);

    RegistryObject<RingSpawner.Type> RING_SPAWN = REGISTRY.register("ring", RingSpawner.Type::new);
    RegistryObject<PointSpawner.Type> POINT_SPAWN = REGISTRY.register("point", PointSpawner.Type::new);
    RegistryObject<EntityBBSpawner.Type> ENTITY_BB = REGISTRY.register("entity_bb", EntityBBSpawner.Type::new);
}
