package net.kapitencraft.kap_lib.inventory.wearable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * item extension for wearables (see {@link net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage EquipmentPage} for more info)
 */
public interface IWearable {

    default Multimap<Attribute, AttributeModifier> getModifiers(WearableSlot slot, ItemStack stack) {
        return ImmutableMultimap.of();
    }
}
