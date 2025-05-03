package net.kapitencraft.kap_lib.item.bonus;

import net.minecraft.resources.ResourceLocation;

public interface AbstractBonusElement {

    boolean isHidden();

    Bonus<?> getBonus();

    ResourceLocation getId();
}
