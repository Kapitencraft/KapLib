package net.kapitencraft.kap_lib.test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class TestSwordItem extends LibSwordItem {
    public TestSwordItem() {
        super(Tiers.DIAMOND, 10, -2.2f, new Properties());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        modifiers.put(Attributes.LUCK, new AttributeModifier(BaseAttributeUUIDs.LUCK, "BaseAttributeUUIDs check", 20, AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    @Override
    public ResourceKey<DamageType> getType() {
        return DamageTypes.FIREBALL;
    }
}
