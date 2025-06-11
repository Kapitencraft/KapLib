package net.kapitencraft.kap_lib.registry.vanilla;

import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.page.crafting.CraftingPage;
import net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaInventoryPages {
    DeferredRegister<InventoryPageType<?>> REGISTRY = DeferredRegister.create(ExtraRegistries.Keys.INVENTORY_PAGES, "minecraft");

    RegistryObject<InventoryPageType<CraftingPage>> CRAFTING = REGISTRY.register("crafting", () -> new InventoryPageType<>(CraftingPage::new));
    RegistryObject<InventoryPageType<EquipmentPage>> EQUIPMENT = REGISTRY.register("equipment", () -> new InventoryPageType<>(EquipmentPage::new));


}
