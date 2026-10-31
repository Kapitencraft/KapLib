package net.kapitencraft.kap_lib.component.event;

import net.kapitencraft.kap_lib.component.ComponentRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class ComponentEvents {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.register(ComponentRegistries.GLYPH_EFFECTS);
    }
}
