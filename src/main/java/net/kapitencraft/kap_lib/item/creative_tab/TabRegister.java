package net.kapitencraft.kap_lib.item.creative_tab;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KapLibMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabRegister {

    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent event) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
        TabGroup.registerAll(event.getTabKey(), entries);
    }
}
