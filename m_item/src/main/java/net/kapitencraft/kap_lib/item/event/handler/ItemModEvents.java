package net.kapitencraft.kap_lib.item.event.handler;

import net.kapitencraft.kap_lib.item.creative_tab.TabGroup;
import net.kapitencraft.kap_lib.item.misc.AnvilUses;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber
@ApiStatus.Internal
public class ItemModEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        AnvilUses.registerUses();
    }

    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent event) {
        TabGroup.registerAll(event);
    }
}
