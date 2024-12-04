package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class ExtraEnchantmentCategories {
    public static final EnchantmentCategory SHIELD = EnchantmentCategory.create("shield", (item)-> item instanceof ShieldItem);
    public static final EnchantmentCategory ALL_WEAPONS = EnchantmentCategory.create("all_weapons", item -> EnchantmentCategory.WEAPON.canEnchant(item) || EnchantmentCategory.BOW.canEnchant(item) || EnchantmentCategory.CROSSBOW.canEnchant(item));
    public static final EnchantmentCategory TOOL = EnchantmentCategory.create("TOOL", item -> item instanceof DiggerItem || ALL_WEAPONS.canEnchant(item));
    public static final EnchantmentCategory FARMING_TOOLS = EnchantmentCategory.create("FARMING_TOOLS", item -> item instanceof HoeItem || item instanceof AxeItem);
}