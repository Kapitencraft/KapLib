package net.kapitencraft.kap_lib.camera.event.handler;

import net.kapitencraft.kap_lib.camera.registry.custom.CameraRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class CameraModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        CameraRegistries.registerAll(event::register);
    }
}
