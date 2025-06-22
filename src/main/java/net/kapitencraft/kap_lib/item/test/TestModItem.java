package net.kapitencraft.kap_lib.item.test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.inventory.wearable.WearableItem;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.registry.custom.WearableSlots;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TestModItem extends WearableItem {

    public TestModItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(WearableSlot slot, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (slot.is(WearableSlots.BELT)) {
            multimap.put(Attributes.LUCK, new AttributeModifier(BaseAttributeUUIDs.LUCK, "Lucky Belt Modifier", 10, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public WearableSlot getSlot() {
        return WearableSlots.BELT.get();
    }
}
