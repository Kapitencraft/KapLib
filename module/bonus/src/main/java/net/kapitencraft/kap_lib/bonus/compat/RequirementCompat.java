package net.kapitencraft.kap_lib.bonus.compat;

import net.kapitencraft.kap_lib.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.event.custom.RegisterRequirementTypesEvent;
import net.minecraft.world.entity.LivingEntity;

public class RequirementCompat {

    public static void onRegisterRequirementTypes(RegisterRequirementTypesEvent event) {
        event.add(BonusRequirementType.INSTANCE);
    }

    public static boolean isBonusActive(AbstractBonusElement bonusEffect, LivingEntity living) {
        return RequirementManager.instance.meetsRequirements(BonusRequirementType.INSTANCE, bonusEffect, living);
    }
}