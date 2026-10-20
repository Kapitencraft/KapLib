package net.kapitencraft.kap_lib.particle.registry.particle_animation;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.terminators.BonusRemovedTerminator;
import net.kapitencraft.kap_lib.particle.animation.terminators.EntityRemovedTerminatorTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface TerminatorTriggers {

    DeferredRegister<TerminationTrigger<?>> REGISTRY = LibConstants.registry(ParticleAnimationRegistries.Keys.TERMINATOR_TYPES);

    Supplier<EntityRemovedTerminatorTrigger> ENTITY_REMOVED = REGISTRY.register("entity_removed", EntityRemovedTerminatorTrigger::new);
    Supplier<TimedTerminator> TIMED = REGISTRY.register("timed", TimedTerminator::new);
    Supplier<BonusRemovedTerminator> BONUS_REMOVED = REGISTRY.register("bonus_removed", BonusRemovedTerminator::new);
}