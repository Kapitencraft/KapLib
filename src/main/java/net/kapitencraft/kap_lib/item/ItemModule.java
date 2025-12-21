package net.kapitencraft.kap_lib.item;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ItemModule.MODULE_ID)
public class ItemModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_item";

    public ItemModule(IEventBus modEventBus, ModContainer container) {

    }
}
