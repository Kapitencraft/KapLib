package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public interface ExtraEnchantmentCategories {
    EnchantmentCategory SHIELD = EnchantmentCategory.create("SHIELD", (item)-> item instanceof ShieldItem);
    EnchantmentCategory MOTION_ARMOR = EnchantmentCategory.create("MOTION_ARMOR", item -> EnchantmentCategory.ARMOR_FEET.canEnchant(item) || EnchantmentCategory.ARMOR_LEGS.canEnchant(item));
    EnchantmentCategory ALL_WEAPONS = EnchantmentCategory.create("ALL_WEAPONS", item -> EnchantmentCategory.WEAPON.canEnchant(item) || EnchantmentCategory.BOW.canEnchant(item) || EnchantmentCategory.CROSSBOW.canEnchant(item));
    EnchantmentCategory RANGED_WEAPONS = EnchantmentCategory.create("RANGED_WEAPONS", item -> EnchantmentCategory.BOW.canEnchant(item) || EnchantmentCategory.CROSSBOW.canEnchant(item));
    EnchantmentCategory TOOL = EnchantmentCategory.create("TOOL", item -> item instanceof DiggerItem || ALL_WEAPONS.canEnchant(item));
    EnchantmentCategory HOE = EnchantmentCategory.create("HOE", item -> item instanceof HoeItem);
    EnchantmentCategory AXE = EnchantmentCategory.create("AXE", item -> item instanceof AxeItem);
    EnchantmentCategory SHEARS = EnchantmentCategory.create("SHEARS", item -> item instanceof ShearsItem);
    EnchantmentCategory FARMING_TOOLS = EnchantmentCategory.create("FARMING_TOOLS", item -> HOE.canEnchant(item) || AXE.canEnchant(item));
}