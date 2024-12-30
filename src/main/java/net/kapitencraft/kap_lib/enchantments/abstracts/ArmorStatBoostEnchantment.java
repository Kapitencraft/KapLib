package net.kapitencraft.kap_lib.enchantments.abstracts;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.function.Consumer;

public interface ArmorStatBoostEnchantment extends StatBoostEnchantment, IArmorEnchantment {

    @Override
    default Consumer<Multimap<Attribute, AttributeModifier>> getModifiers(int level, ItemStack enchanted, EquipmentSlot slot) {
        if (enchanted.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == slot) {
            return getArmorModifiers(level, enchanted, slot);
        }
        return multimap -> {};
    }

    Consumer<Multimap<Attribute, AttributeModifier>> getArmorModifiers(int level, ItemStack enchanted, EquipmentSlot slot);

    @Override
    default boolean hasModifiersForThatSlot(EquipmentSlot slot, ItemStack stack) {
        return MiscHelper.getSlotForStack(stack) == slot;
    }

    @Override
    default List<EquipmentSlot> slots() {
        return List.of(MiscHelper.ARMOR_EQUIPMENT);
    }
}
