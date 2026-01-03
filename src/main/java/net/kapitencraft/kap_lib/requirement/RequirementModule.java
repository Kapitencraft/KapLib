package net.kapitencraft.kap_lib.requirement;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.requirement.registry.RequirementConditionTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(RequirementModule.MODULE_ID)
public class RequirementModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_requirement";

    public RequirementModule(IEventBus modEventBus, ModContainer container) {
        RequirementConditionTypes.REGISTRY.register(modEventBus);
    }
}
