package net.kapitencraft.kap_lib.bonus;

import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.core.util.Modules;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * a bonus element. override if you wish to add other means of bonuses being applied to entities
 */
public interface AbstractBonusElement {

    /**
     * determines whether this bonus should be visible in tooltips
     * @return whether this bonus should be visible in tooltips
     */
    boolean isHidden();

    /**
     * supplies the bonus
     * @return the bonus this element contains
     */
    Bonus<?> getBonus();

    /**
     * supplies the location of this element, useful for identifying the element
     * @return the location of this element
     */
    ResourceLocation getId();

    /**
     * @return the bonus type title
     */
    MutableComponent getTitle();

    /**
     * @return the name ID of this element. used translations
     */
    String getNameId();

    default boolean isActive(LivingEntity living) {
        return !Modules.isRequirementsActive() || RequirementManager.instance.meetsRequirements(BonusRequirementType.INSTANCE, this, living);
    }
}