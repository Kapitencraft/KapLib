package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.*;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface SpawnerTypes {

    DeferredRegister<VisibleSpawner.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.SPAWNER_TYPES);

    Supplier<RingSpawner.Type> RING = REGISTRY.register("ring", RingSpawner.Type::new);
    Supplier<TrackingSpawner.Type> TRACKING = REGISTRY.register("point", TrackingSpawner.Type::new);
    Supplier<EntityBBSpawner.Type> ENTITY_BB = REGISTRY.register("entity_bb", EntityBBSpawner.Type::new);
    Supplier<LineSpawner.Type> LINE = REGISTRY.register("line", LineSpawner.Type::new);
    Supplier<GroupSpawner.Type> GROUP = REGISTRY.register("group", GroupSpawner.Type::new);
}
