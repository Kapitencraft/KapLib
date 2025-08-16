package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.event.custom.ItemStackEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModEventFactory {

    public static <T extends Event & IModBusEvent> void fireModEvent(T event) {
        ModLoader.postEvent(event);
    }

    public static void onLoadingItemStack(ItemStack stack) {
        ItemStackEvent.Load event = new ItemStackEvent.Load(stack);
        NeoForge.EVENT_BUS.post(event);
    }

    public static void onSavingItemStack(ItemStack stack, CompoundTag tag) {
        ItemStackEvent.Save event = new ItemStackEvent.Save(stack, tag);
        NeoForge.EVENT_BUS.post(event);
    }
}