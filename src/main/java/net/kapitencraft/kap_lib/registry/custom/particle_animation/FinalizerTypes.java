package net.kapitencraft.kap_lib.registry.custom.particle_animation;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.EmptyFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface FinalizerTypes {

    DeferredRegister<ParticleFinalizer.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.FINALIZER_TYPES);

    Supplier<RemoveParticleFinalizer.Type> REMOVE_PARTICLE = REGISTRY.register("remove_particle", RemoveParticleFinalizer.Type::new);

    Supplier<EmptyFinalizer.Type> EMPTY = REGISTRY.register("empty", EmptyFinalizer.Type::new);

    Supplier<SetLifeTimeFinalizer.Type> SET_LIFE_TIME = REGISTRY.register("set_life_time", SetLifeTimeFinalizer.Type::new);
}
