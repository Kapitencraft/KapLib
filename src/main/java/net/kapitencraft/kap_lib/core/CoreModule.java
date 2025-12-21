package net.kapitencraft.kap_lib.core;

import net.kapitencraft.kap_lib.core.config.CoreClientModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * necessary module for all other modules
 */
@Mod(CoreModule.MODULE_ID)
public class CoreModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_core";

    public CoreModule(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CoreClientModConfig.SPEC);
    }
}
