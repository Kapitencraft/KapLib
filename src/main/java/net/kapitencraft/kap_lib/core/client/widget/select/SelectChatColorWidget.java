package net.kapitencraft.kap_lib.core.client.widget.select;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.core.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SelectChatColorWidget extends PositionedWidget {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Consumer<ColorType> valueSink;
    private final Component title;
    private final Font font;
    private ColorType value;

    public SelectChatColorWidget(int x, int y, Consumer<ColorType> valueSink, Component title, Font font, ColorType value) {
        super(x, y, 100, 136);
        this.valueSink = valueSink;
        this.title = title;
        this.font = font;
        if (font.width(title) > 90) LOGGER.warn("title for chat color select wider than feasible");
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

    public static final ColorType[] COLOR_TYPES = createColorTypes();
    public static final TextColor[] COLOR_LOOKUP = createLookup();

    private static TextColor[] createLookup() {
        return Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(TextColor::fromLegacyFormat).toArray(TextColor[]::new);
    }

    private static ColorType[] createColorTypes() {
        List<ColorType> types = Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(VanillaColorType::new).collect(Collectors.toCollection(ArrayList::new));
        //if (Modules.isComponentActive()) {
        //    ComponentCompat.appendChromaType(types);
        //}
        return types.toArray(ColorType[]::new);
    }

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
}
