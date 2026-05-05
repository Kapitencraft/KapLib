package net.kapitencraft.kap_lib.particle.event.handler;

import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;


@EventBusSubscriber
@ApiStatus.Internal
public class ModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        ParticleAnimationRegistries.registerAll(event::register);
    }
}
