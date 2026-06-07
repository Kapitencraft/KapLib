package net.kapitencraft.kap_lib.particle.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ParticleAnimationRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = syncReg(Keys.MODIFIER_TYPES);
    Registry<Spawner.Type<?>> SPAWNER_TYPES = syncReg(Keys.SPAWNER_TYPES);
    Registry<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = syncReg(Keys.FINALIZER_TYPES);
    Registry<TerminationTrigger<?>> TERMINATION_TRIGGERS = syncReg(Keys.TERMINATOR_TYPES);
    Registry<ActivationTrigger<?>> ACTIVATION_TRIGGERS = syncReg(Keys.ACTIVATION_TRIGGERS);

    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).sync(true).create();
        registries.add(registry);
        return registry;
    }

    interface Keys {
        ResourceKey<Registry<AnimationElement.Type<?>>> MODIFIER_TYPES = createRegistry("particle_animation/element_types");
        ResourceKey<Registry<Spawner.Type<?>>> SPAWNER_TYPES = createRegistry("particle_animation/spawner_types");
        ResourceKey<Registry<ParticleFinalizer.Type<?>>> FINALIZER_TYPES = createRegistry("particle_animation/finalizer_types");
        ResourceKey<Registry<TerminationTrigger<?>>> TERMINATOR_TYPES = createRegistry("particle_animation/terminator_types");
        ResourceKey<Registry<ActivationTrigger<?>>> ACTIVATION_TRIGGERS = createRegistry("particle_animation/activation_triggers");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }
}
