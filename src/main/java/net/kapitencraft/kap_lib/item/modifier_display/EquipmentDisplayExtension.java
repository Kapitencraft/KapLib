package net.kapitencraft.kap_lib.item.modifier_display;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * used to display all attribute modifications made by this to the item's tooltip.
 * <br>only implement in custom bonuses
 * <br>register under {@link net.kapitencraft.kap_lib.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent RegisterItemModifiersDisplayExtensionsEvent}
 */
//TODO make more elegantly
public interface EquipmentDisplayExtension extends DisplayExtension<EquipmentSlot> {

}
