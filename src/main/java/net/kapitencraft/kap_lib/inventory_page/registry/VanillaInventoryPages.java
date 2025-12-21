package net.kapitencraft.kap_lib.inventory_page.registry;

import net.kapitencraft.kap_lib.inventory_page.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory_page.page.crafting.CraftingPage;
import net.kapitencraft.kap_lib.inventory_page.page.equipment.EquipmentPage;
import net.kapitencraft.kap_lib.inventory_page.registry.custom.InventoryPageRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface VanillaInventoryPages {
    DeferredRegister<InventoryPageType<?>> REGISTRY = DeferredRegister.create(InventoryPageRegistries.Keys.INVENTORY_PAGES, "minecraft");

    Supplier<InventoryPageType<CraftingPage>> CRAFTING = REGISTRY.register("crafting", () -> new InventoryPageType<>(CraftingPage::new));
    Supplier<InventoryPageType<EquipmentPage>> EQUIPMENT = REGISTRY.register("equipment", () -> new InventoryPageType<>(EquipmentPage::new));


}
