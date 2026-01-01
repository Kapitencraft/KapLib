package net.kapitencraft.kap_lib.item.modifier_display;

import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.NotNull;

public interface DisplayExtension<T> {

    default Component createComponent(double value) {
        Type type = getType();
        if (type == Type.NONE) return CommonComponents.EMPTY;
        return Component.literal(type.open +
                (value > 0 ? "+" : "") +
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
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ExtraStreamCodecs.enumCodec(Type.values());

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
