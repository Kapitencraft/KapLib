package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface WearableSlots {

    DeferredRegister<WearableSlot> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.WEARABLE_SLOTS);

    RegistryObject<WearableSlot> NECKLACE = REGISTRY.register("necklace", () -> new WearableSlot(45, 8));
    RegistryObject<WearableSlot> CLOAK = REGISTRY.register("cloak", () -> new WearableSlot(45, 26));
    RegistryObject<WearableSlot> BELT = REGISTRY.register("belt", () -> new WearableSlot(45, 44));
    RegistryObject<WearableSlot> GLOVE = REGISTRY.register("glove", () -> new WearableSlot(45, 62));
}
