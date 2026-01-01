package net.kapitencraft.kap_lib.bonus;

import net.kapitencraft.kap_lib.bonus.compat.RequirementTypesCompat;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.bonus.registry.BonusTypes;
import net.kapitencraft.kap_lib.core.util.Modules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(BonusModule.MODULE_ID)
public class BonusModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_bonus";

    public BonusModule(IEventBus modEventBus, ModContainer container) {
        BonusTypes.REGISTRY.register(modEventBus);

        if (Modules.isRequirementsActive()) {
            NeoForge.EVENT_BUS.addListener(RequirementTypesCompat::onRegisterRequirementTypes);
        }
    }
}
