package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EitherTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EntityRemovedTerminator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface TerminatorTypes {

    DeferredRegister<AnimationTerminator.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.TERMINATOR_TYPES);

    RegistryObject<EntityRemovedTerminator.Type> ENTITY_REMOVED = REGISTRY.register("entity_removed", EntityRemovedTerminator.Type::new);
    RegistryObject<TimedTerminator.Type> TIMED = REGISTRY.register("timed", TimedTerminator.Type::new);
    RegistryObject<EitherTerminator.Type> EITHER = REGISTRY.register("either", EitherTerminator.Type::new);
}
