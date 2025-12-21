package net.kapitencraft.kap_lib.attribute;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(AttributeModule.MODULE_ID)
public class AttributeModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_attribute";

    public AttributeModule(IEventBus modEventBus, ModContainer container) {
        ExtraAttributes.REGISTRY.register(modEventBus);
        AttributeAttachmentTypes.REGISTRY.register(modEventBus);
    }
}
