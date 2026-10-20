package net.kapitencraft.kap_lib.bonus.compat;

import net.kapitencraft.kap_lib.bonus.type.AttributeModifiersBonus;
import net.kapitencraft.kap_lib.item.modifier_display.DisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.EquipmentDisplayExtension;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class ItemCompat {

    /**
     * creates a display extension for the given attribute modifier bonus
     */
    public static EquipmentDisplayExtension extensionFor(AttributeModifiersBonus bonus, Style style, DisplayExtension.Type type) {
        return new EquipmentDisplayExtension() {
            @Override
            public ResourceLocation getModifiersLocation() {
                return bonus.getLocation();
            }

            @Override
            public Style getStyle() {
                return style;
            }

            @Override
            public Type getType() {
                return type;
            }
        };
    }
}
