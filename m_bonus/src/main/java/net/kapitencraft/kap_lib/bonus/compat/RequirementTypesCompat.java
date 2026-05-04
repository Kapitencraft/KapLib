package net.kapitencraft.kap_lib.bonus.compat;

import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.requirement.event.custom.RegisterRequirementTypesEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class RequirementTypesCompat {

    public static void onRegisterRequirementTypes(RegisterRequirementTypesEvent event) {
        event.add(BonusRequirementType.INSTANCE);
    }
}
