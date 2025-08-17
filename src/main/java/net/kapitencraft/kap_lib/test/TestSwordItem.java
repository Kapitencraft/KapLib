package net.kapitencraft.kap_lib.test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

public class TestSwordItem extends LibSwordItem {
    public TestSwordItem() {
        super(Tiers.DIAMOND, 10, -2.2f, new Properties());
    }

    @Override
    public @NotNull ResourceKey<DamageType> getDamageType() {
        return DamageTypes.FIREBALL;
    }
}
