package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class SimpleToolEnchantment extends Enchantment {
    protected SimpleToolEnchantment(Rarity p_44676_) {
        super(p_44676_, ExtraEnchantmentCategories.TOOL, MiscHelper.WEAPON_SLOT);
    }
}
