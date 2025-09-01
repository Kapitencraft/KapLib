package net.kapitencraft.kap_lib.item.modifier_display;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.NotNull;

public interface DisplayExtension<T> {

    default Component createComponent(double value) {
        Type type = getType();
        if (type == Type.NONE) return CommonComponents.EMPTY;
        return Component.literal(type.open +
                IAttributeExtension.FORMAT.format(value) +
                type.close
        ).withStyle(getStyle());
    }

    /**
     * @return the RL used by any item modifier this extension provides
     */
    ResourceLocation getModifiersLocation();

    Style getStyle();

    Type getType();

    enum Type implements StringRepresentable {
        NONE(' ', ' '),
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
