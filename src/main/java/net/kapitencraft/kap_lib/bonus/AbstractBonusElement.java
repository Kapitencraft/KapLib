package net.kapitencraft.kap_lib.bonus;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * a bonus element. override if you wish to add other means of bonuses being applied to entities
 */
public interface AbstractBonusElement {

    /**
     * @return whether this bonus should be visible in tooltips
     */
    boolean isHidden();

    /**
     * @return the bonus this element contains
     */
    Bonus<?> getBonus();

    /**
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
}