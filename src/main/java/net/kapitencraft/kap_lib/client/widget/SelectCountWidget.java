package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.util.range.simple.IntegerNumberRange;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SelectCountWidget extends PositionedWidget {
    private final Consumer<Integer> valueSink;
    private final IntegerNumberRange range;
    private boolean dragging;
    private int value;
    private final Component title;
    private final Font font;

    public SelectCountWidget(int x, int y, int width, Font font, int value, Consumer<Integer> valueSink, IntegerNumberRange range, Component title) {
        super(x, y, width, 45);
        this.valueSink = valueSink;
        this.range = range;
        this.title = title;
        this.font = font;
        this.value = value;
    }

    int getMinX() {
        return this.x + 10 + (value - range.getMin()) * (width - 25) / range.getRange();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.x, this.y, this.getMaxX(), this.getMaxY(), 0xFF404040);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.x + this.width / 2, this.y + 2, 0xFFFFFF);
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("number_value", this.value), this.x + this.width / 2, this.y + 12, 0xFFFFFF);
        pGuiGraphics.fill(this.x + 10, this.getMaxY() - 15, this.getMaxX() - 10, this.getMaxY() - 10, 0xFFFFFFFF);
        int minVal = getMinX();
        pGuiGraphics.fill(minVal, this.getMaxY() - 20, minVal + 5, this.getMaxY() - 5, dragging ? 0xFFB7B7B7 : 0xFF707070);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int minVal = getMinX();
        if (MathHelper.is2dBetween(pMouseX, pMouseY, minVal, getMaxY() - 15, minVal + 5, getMaxY() - 10)) {
            dragging = true;
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (dragging) {
            int delta = Math.max((int) pMouseX - (this.x + 10), 0);
            int value = Math.min(this.range.getMin() + delta * this.range.getRange() / (width - 20), this.range.getMax());
            if (this.value != value) {
                this.valueSink.accept(value);
                this.value = value;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.dragging = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
