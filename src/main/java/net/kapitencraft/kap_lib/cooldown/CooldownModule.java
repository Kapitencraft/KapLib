package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.enchantment.ExtraEnchantmentEffectComponents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CooldownModule.MODULE_ID)
public class CooldownModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_cooldown";

    public CooldownModule(IEventBus modEventBus, ModContainer container) {
        CooldownAttributes.REGISTRY.register(modEventBus);
    }
}
