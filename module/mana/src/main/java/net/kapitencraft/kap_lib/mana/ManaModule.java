package net.kapitencraft.kap_lib.mana;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.mana.advancement.ExtraCriterionTriggers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ManaModule.MODULE_ID)
public class ManaModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_mana";

    public ManaModule(IEventBus modEventBus, ModContainer container) {
        ExtraCriterionTriggers.REGISTRY.register(modEventBus);
        ManaAttributes.REGISTRY.register(modEventBus);
        ManaAttachmentTypes.REGISTRY.register(modEventBus);
    }
}
