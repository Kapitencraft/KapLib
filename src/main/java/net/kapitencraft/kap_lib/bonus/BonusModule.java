package net.kapitencraft.kap_lib.bonus;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.bonus.registry.BonusTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(BonusModule.MODULE_ID)
public class BonusModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_bonus";

    public BonusModule(IEventBus modEventBus, ModContainer container) {
        BonusTypes.REGISTRY.register(modEventBus);
    }
}
