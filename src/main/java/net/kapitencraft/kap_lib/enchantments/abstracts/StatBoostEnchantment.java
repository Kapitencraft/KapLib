package net.kapitencraft.kap_lib.enchantments.abstracts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

//TODO add reqs
public interface StatBoostEnchantment extends ModEnchantment {
    List<EquipmentSlot> slots();

    Consumer<Multimap<Attribute, AttributeModifier>> getModifiers(int level, ItemStack enchanted, EquipmentSlot slot);

    default boolean hasModifiersForThatSlot(EquipmentSlot slot, ItemStack stack) {
        return this.slots().contains(slot);
    }

    static Multimap<Attribute, AttributeModifier> getAllModifiers(ItemStack stack, EquipmentSlot slot) {
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        MapStream.of(enchantments).filterKeys(ench -> ench instanceof StatBoostEnchantment)
                .mapKeys(StatBoostEnchantment.class::cast)
                .filterKeys(boostEnchantment -> boostEnchantment.hasModifiersForThatSlot(slot, stack))
                .mapToSimple((boostEnchantment, integer) -> boostEnchantment.getModifiers(integer, stack, slot))
                .forEach(multimapConsumer -> multimapConsumer.accept(multimap));
        return multimap;
    }
}
