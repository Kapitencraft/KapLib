package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectEnumWidget<T> extends PositionedWidget {
    private static final Component SELECT_PANEL = Component.translatable("mco.template.button.select");

    private final Font font;
    private final List<T> values;
    private final Function<T, Component> nameExtractor;
    private final Consumer<T> valueSink;
    private final Component title;
    private final int titleWidth;
    private T selected;

    public SelectEnumWidget(int x, int y, int width, int height, Font font, List<T> values, Function<T, Component> nameExtractor, Consumer<T> valueSink, Component title) {
        super(x, y, width, height);
        this.font = font;
        this.values = values;
        this.nameExtractor = nameExtractor;
        this.valueSink = valueSink;
        this.title = title;
        this.titleWidth = this.font.width(title);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.x, this.y, this.getMaxX(), this.getMaxY(), 0xFF404040);
        pGuiGraphics.drawCenteredString(font, title, this.x + width / 2, this.y + 1, 0xFFFFFF);
        pGuiGraphics.enableScissor(this.x + 2, this.y + 12, this.getMaxX() - 2, this.getMaxY() - 12);
        int selectedIndex = (pMouseY - this.y - 12) / 10;
        for (int i = 0; i < values.size(); i++) {
            T t = values.get(i);
            int y = this.y + 12 + i * 10;
            pGuiGraphics.fill(this.x + 2, y - 1, this.getMaxX() - 3, y + 9, selected == t ? 0xFF808080 : i == selectedIndex ? 0xFF707070 : 0xFF606060);
            pGuiGraphics.drawString(font, nameExtractor.apply(t), this.x + 3, y, 0xFFFFFF);
        }
        pGuiGraphics.disableScissor();
        pGuiGraphics.drawString(this.font, SELECT_PANEL, this.getMaxX() - (titleWidth + 2), this.getMaxY() - 10, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        //pMouseY = this.y + 12 + i * 10
        int selectedIndex = ((int) pMouseY - this.y - 12) / 10;
        if (MathHelper.validIndex(values, selectedIndex)) {
            selected = values.get(selectedIndex);
            return true;
        }

        int selectPos = this.getMaxX() - (titleWidth + 2);
        if (MathHelper.is2dBetween(pMouseX, pMouseY, selectPos - 1, this.getMaxY() - 10, selectPos + titleWidth + 1, this.getMaxY())) {
            if (this.selected != null) {
                this.valueSink.accept(selected);
                this.selected = null;
                return true;
            }
        }

        return false;
    }
}
