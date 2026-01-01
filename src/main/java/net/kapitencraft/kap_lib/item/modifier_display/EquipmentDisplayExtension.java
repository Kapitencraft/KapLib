package net.kapitencraft.kap_lib.item.modifier_display;

import net.kapitencraft.kap_lib.item.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * used to display all attribute modifications made by this to the item's tooltip.
 * <br>only implement in custom bonuses
 * <br>register under {@link RegisterItemModifiersDisplayExtensionsEvent RegisterItemModifiersDisplayExtensionsEvent}
 */
public interface EquipmentDisplayExtension extends DisplayExtension<EquipmentSlot> {

}
