package net.kapitencraft.kap_lib.component.event.handler;

import net.kapitencraft.kap_lib.component.registry.custom.ComponentRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class ComponentModEvents {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        ComponentRegistries.registerAll(event::register);
    }
}