package net.kapitencraft.kap_lib.registry.custom;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.Holder;

public interface WearableSlots {

    DeferredRegister<WearableSlot> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.WEARABLE_SLOTS);

    Holder<WearableSlot> NECKLACE = REGISTRY.register("necklace", () -> new WearableSlot(45, 8, Pair.of(InventoryMenu.BLOCK_ATLAS, KapLibMod.res("item/empty_necklace_slot"))));
    Holder<WearableSlot> CLOAK = REGISTRY.register("cloak", () -> new WearableSlot(45, 26, Pair.of(InventoryMenu.BLOCK_ATLAS, KapLibMod.res("item/empty_glove_slot"))));
    Holder<WearableSlot> BELT = REGISTRY.register("belt", () -> new WearableSlot(45, 44, Pair.of(InventoryMenu.BLOCK_ATLAS, KapLibMod.res("item/empty_belt_slot"))));
    Holder<WearableSlot> GLOVE = REGISTRY.register("glove", () -> new WearableSlot(45, 62, Pair.of(InventoryMenu.BLOCK_ATLAS, KapLibMod.res("item/empty_cloak_slot"))));
}
