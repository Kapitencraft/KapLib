package net.kapitencraft.kap_lib.shader;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.shader.config.ShaderClientModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(ShaderModule.MODULE_ID)
public class ShaderModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_shader";

    public ShaderModule(IEventBus modEventBus, ModContainer container) {

        container.registerConfig(ModConfig.Type.CLIENT, ShaderClientModConfig.SPEC);
    }
}
