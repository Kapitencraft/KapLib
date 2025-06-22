package net.kapitencraft.kap_lib.item.bonus;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

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

    String getNameId();
}