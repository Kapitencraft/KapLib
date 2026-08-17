package net.kapitencraft.kap_lib.inventory_page.event.handler;

import net.kapitencraft.kap_lib.inventory_page.InventoryPageModule;
import net.kapitencraft.kap_lib.inventory_page.registry.custom.InventoryPageRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;


@EventBusSubscriber(modid = InventoryPageModule.MODULE_ID)
@ApiStatus.Internal
public class InventoryPageEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        InventoryPageRegistries.registerAll(event::register);
    }
}
