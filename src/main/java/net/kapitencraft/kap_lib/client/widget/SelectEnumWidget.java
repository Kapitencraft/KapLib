package net.kapitencraft.kap_lib.client.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public class SelectEnumWidget<T> extends PositionedWidget {
    private final Font font;
    private final List<T> values;
    private final Function<T, Component> nameExtractor;

    protected SelectEnumWidget(int x, int y, int width, int height, Font font, List<T> values, Function<T, Component> nameExtractor) {
        super(x, y, width, height);
        this.font = font;
        this.values = values;
        this.nameExtractor = nameExtractor;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
