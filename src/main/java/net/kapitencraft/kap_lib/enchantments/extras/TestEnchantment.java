package net.kapitencraft.kap_lib.enchantments.extras;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.enchantments.abstracts.ArmorStatBoostEnchantment;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Consumer;

public class TestEnchantment extends ArmorStatBoostEnchantment {

    public TestEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, MiscHelper.ARMOR_EQUIPMENT);
    }

    @Override
    public Consumer<Multimap<Attribute, AttributeModifier>> getArmorModifiers(int level, ItemStack enchanted, EquipmentSlot slot) {
        return multimap -> multimap.put(ExtraAttributes.MINING_SPEED.get(), AttributeHelper.createModifierForSlot("test", AttributeModifier.Operation.ADDITION, level * 2, slot));
    }
}
