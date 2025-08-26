package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.EntityAddedTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface ActivationTriggers {

    DeferredRegister<ActivationTrigger<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.ACTIVATION_TRIGGERS);

    RegistryObject<EntityAddedTrigger> ENTITY_ADDED = REGISTRY.register("entity_added", EntityAddedTrigger::new);
}
