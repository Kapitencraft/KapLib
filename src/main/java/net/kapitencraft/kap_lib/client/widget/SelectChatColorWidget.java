package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColor;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.shaders.ModRenderTypes;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.custom.GlyphEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SelectChatColorWidget extends PositionedWidget {
    private final Consumer<ColorType> valueSink;
    private final Component title;
    private final Font font;
    private ColorType value;

    public SelectChatColorWidget(int x, int y, Consumer<ColorType> valueSink, Component title, Font font, ColorType value) {
        super(x, y, 100, 136);
        this.valueSink = valueSink;
        this.title = title;
        this.font = font;
        if (font.width(title) > 90) KapLibMod.LOGGER.warn("title for chat color select wider than feasible");
        this.value = value;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.x, this.y, this.getMaxX(), this.getMaxY(), 0xFF404040);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.x + width / 2, this.y + 2, 0xFFFFFF);
        for (int i = 0; i < 17; i++) {
            ColorType type = COLOR_TYPES[i];
            int x = this.x + 2 + (i % 4) * 24;
            int y = this.y + 14 + (i / 4) * 24;
            int xOffset = i % 4;
            if (type == value) {
                pGuiGraphics.fill(x - 1, y - 1, x + 21, y + 21, 0xC0C0C0);
            }
            type.render(pGuiGraphics, x, y, 20);
        }
    }

    private static final ColorType[] COLOR_TYPES = createColorTypes();
    private static final TextColor[] COLOR_LOOKUP = createLookup();

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (int i = 0; i < 17; i++) {
            int x = this.x + 2 + (i % 4) * 22;
            int y = this.y + 14 + (i / 4) * 22;
            if (MathHelper.is2dBetween(pMouseX, pMouseY, x, y, x + 20, y + 20)) {
                ColorType type = COLOR_TYPES[i];
                if (this.value != type) {
                    this.valueSink.accept(type);
                    this.value = type;
                }
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static TextColor[] createLookup() {
        return Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(TextColor::fromLegacyFormat).toArray(TextColor[]::new);
    }

    private static final int COLOR_TYPE_COUNT = 17;

    public static ColorType getColor(EnchantmentColor color) {
        Style style = color.targetStyle();
        EffectsStyle effectsStyle = EffectsStyle.of(style);
        if (effectsStyle.hasEffect(GlyphEffects.RAINBOW.get())) return COLOR_TYPES[16]; //get the rainbow element
        for (int i = 0; i < 16; i++) {
            if (COLOR_LOOKUP[i].equals(style.getColor())) return COLOR_TYPES[i];
        }
        throw new IllegalArgumentException("could not extract color from style: " + style);
    }

    private static ColorType[] createColorTypes() {
        List<ColorType> types = Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(VanillaColorType::new).collect(Collectors.toCollection(ArrayList::new));
        types.add(new ChromaColorType());
        return types.toArray(ColorType[]::new);
    }

    private static final int COLOR_SIZE = 10;

    public interface ColorType {

        void render(GuiGraphics graphics, int x, int y, int width);

        Style getStyle();
    }

    private record VanillaColorType(ChatFormatting formatting, Style formattedStyle) implements ColorType {
        public VanillaColorType(@NotNull ChatFormatting formatting) {
            this(formatting, Style.EMPTY.withColor(formatting));
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public void render(GuiGraphics graphics, int x, int y, int width) {
            graphics.fill(x, y, x + width, y + width, 0xFF000000 | formatting.getColor());
        }

        @Override
        public Style getStyle() {
            return formattedStyle;
        }
    }

    private static class ChromaColorType implements ColorType {
        private static final Style CHROMA_STYLE = MiscHelper.withSpecial(Style.EMPTY, GlyphEffects.RAINBOW);

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width) {
            graphics.fill(ModRenderTypes.FILL_CHROMA, x, y, x + width, y + width, 0);
        }

        @Override
        public Style getStyle() {
            return CHROMA_STYLE;
        }
    }
}
