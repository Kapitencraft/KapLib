package net.kapitencraft.kap_lib.particle.registry.particle_animation;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.EntityAddedTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ActivationTriggers {

    DeferredRegister<ActivationTrigger<?>> REGISTRY = LibConstants.registry(ParticleAnimationRegistries.Keys.ACTIVATION_TRIGGERS);

    Supplier<EntityAddedTrigger> ENTITY_ADDED = REGISTRY.register("entity_added", EntityAddedTrigger::new);
}
