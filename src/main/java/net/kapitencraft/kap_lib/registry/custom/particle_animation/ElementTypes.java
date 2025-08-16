package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.elements.*;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ElementTypes {

    DeferredRegister<AnimationElement.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.MODIFIER_TYPES);

    Supplier<StartFadeOutElement.Type> START_FADE_OUT = REGISTRY.register("start_fade_out", StartFadeOutElement.Type::new);
    Supplier<MoveTowardsElement.Type> MOVE_TOWARDS = REGISTRY.register("move_towards", MoveTowardsElement.Type::new);
    Supplier<MoveTowardsBBElement.Type> MOVE_TOWARDS_BB = REGISTRY.register("move_towards_bb", MoveTowardsBBElement.Type::new);
    Supplier<MoveAwayElement.Type> MOVE_AWAY = REGISTRY.register("move_away", MoveAwayElement.Type::new);
    Supplier<KeepAliveElement.Type> KEEP_ALIVE = REGISTRY.register("keep_alive", KeepAliveElement.Type::new);
    Supplier<RotateElement.Type> ROTATE = REGISTRY.register("rotate", RotateElement.Type::new);
    Supplier<GroupElement.Type> GROUP = REGISTRY.register("group", GroupElement.Type::new);
}