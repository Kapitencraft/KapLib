package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.MoveAwayElement;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.MoveTowardsElement;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.StartFadeOutElement;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface ElementTypes {

    DeferredRegister<AnimationElement.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.MODIFICATION_ELEMENT_TYPES);

    RegistryObject<StartFadeOutElement.Type> START_FADE_OUT = REGISTRY.register("start_fade_out", StartFadeOutElement.Type::new);
    RegistryObject<MoveTowardsElement.Type> MOVE_TOWARDS = REGISTRY.register("move_towards", MoveTowardsElement.Type::new);
    RegistryObject<MoveAwayElement.Type> MOVE_AWAY = REGISTRY.register("move_away", MoveAwayElement.Type::new);
}