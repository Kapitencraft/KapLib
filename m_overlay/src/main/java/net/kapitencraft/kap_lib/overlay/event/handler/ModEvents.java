package net.kapitencraft.kap_lib.overlay.event.handler;

import net.kapitencraft.kap_lib.overlay.registry.custom.OverlayRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class ModEvents {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        OverlayRegistries.registerAll(event::register);
    }
}