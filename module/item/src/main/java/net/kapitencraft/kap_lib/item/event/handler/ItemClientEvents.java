package net.kapitencraft.kap_lib.item.event.handler;

import net.kapitencraft.kap_lib.item.ItemModule;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ItemModule.MODULE_ID)
public class ItemClientEvents {

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        ModifierDisplayManager.init();
    }
}
