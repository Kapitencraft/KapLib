package net.kapitencraft.kap_lib.inventory_page.event.handler;

import net.kapitencraft.kap_lib.inventory_page.page_renderer.InventoryPageRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        InventoryPageRenderers.init();
    }

}
