package net.kapitencraft.kap_lib.loot;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemConditions;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemFunctions;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(LootModule.MODULE_ID)
public class LootModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_loot";

    public LootModule(IEventBus modEventBus, ModContainer container) {
        ExtraLootItemConditions.REGISTRY.register(modEventBus);
        ExtraLootItemFunctions.REGISTRY.register(modEventBus);
        ExtraLootModifiers.REGISTRY.register(modEventBus);
    }
}
