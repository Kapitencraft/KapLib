package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.*;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

//TODO re-add either
public interface TerminatorTriggers {

    DeferredRegister<TerminationTrigger<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.TERMINATOR_TYPES);

    Supplier<EntityRemovedTerminatorTrigger> ENTITY_REMOVED = REGISTRY.register("entity_removed", EntityRemovedTerminatorTrigger::new);
    Supplier<TimedTerminator> TIMED = REGISTRY.register("timed", TimedTerminator::new);
    Supplier<BonusRemovedTerminator> BONUS_REMOVED = REGISTRY.register("bonus_removed", BonusRemovedTerminator::new);
}