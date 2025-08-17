package net.kapitencraft.kap_lib.helpers;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentHelper {
    public static Enchantment.Builder ultimate(HolderGetter<Enchantment> enchantments, Enchantment.EnchantmentDefinition definition) {
        return Enchantment.enchantment(definition).exclusiveWith(enchantments.getOrThrow(ExtraTags.Enchantments.ULTIMATE));
    }
}
