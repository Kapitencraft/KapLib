package net.kapitencraft.kap_lib.enchantment;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.enchantment.config.EnchantmentClientModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(EnchantmentModule.MODULE_ID)
public class EnchantmentModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_enchantment";

    public EnchantmentModule(IEventBus modEventBus, ModContainer container) {
        ExtraEnchantmentEffectComponents.REGISTRY.register(modEventBus);

        container.registerConfig(ModConfig.Type.CLIENT, EnchantmentClientModConfig.SPEC);
    }
}
