package net.kapitencraft.kap_lib.inventory_page;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.inventory_page.registry.VanillaInventoryPages;
import net.kapitencraft.kap_lib.inventory_page.registry.WearableAttachmentTypes;
import net.kapitencraft.kap_lib.inventory_page.registry.WearableSlots;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(InventoryPageModule.MODULE_ID)
public class InventoryPageModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_inventory_page";

    public InventoryPageModule(IEventBus modEventBus, ModContainer container) {
        WearableSlots.REGISTRY.register(modEventBus);
        WearableAttachmentTypes.REGISTRY.register(modEventBus);
        VanillaInventoryPages.REGISTRY.register(modEventBus);
    }
}