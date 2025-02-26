package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.util.range.simple.IntegerNumberRange;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class SelectCountWidget extends PositionedWidget {
    private final Consumer<Integer> valueSink;
    private final IntegerNumberRange range;

    protected SelectCountWidget(int x, int y, int width, int height, Consumer<Integer> valueSink, IntegerNumberRange range) {
        super(x, y, width, height);
        this.valueSink = valueSink;
        this.range = range;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }
}
