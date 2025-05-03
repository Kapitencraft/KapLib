package net.kapitencraft.kap_lib.item.modifier_display;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * used to display all attribute modifications made by this to the item's tooltip.
 * only implement in custom bonuses
 */
public interface ItemModifiersDisplayExtension {

    default Component createComponent(double value) {
        Type type = getType();
        return Component.literal(type.open +
                (value < 0 ? "" : "+") +  ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value) +
                type.close
        ).withStyle(getStyle());
    }

    Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot);

    Style getStyle();

    Type getType();

    enum Type implements StringRepresentable {
        CURLY('{', '}'),
        SQUARE('[', ']'),
        DEFAULT('(', ')'),
        POINTY('<', '>');

        public static final EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final char open, close;

        Type(char open, char close) {
            this.open = open;
            this.close = close;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
