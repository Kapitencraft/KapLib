package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.kapitencraft.kap_lib.util.range.simple.NumberRange;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumberElement<T extends Number> extends Element {
    private final NumberRange<T> range;
    private EditBox box;
    private final int width;

    public NumberElement(ListElement parent, DropDownMenu menu, Component name, NumberRange<T> range, int width) {
        super(parent, menu, name);
        this.width = width;
        this.range = range;
    }

    @Override
    public void click(float relativeX, float relativeY) {

    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    public static class Builder<T extends Number> extends Element.Builder<NumberElement<T>, Builder<T>> {
        private int width;
        private NumberRange<T> range;

        public Builder<T> setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder<T> setRange(NumberRange<T> range) {
            this.range = range;
            return this;
        }

        @Override
        public NumberElement<T> build(ListElement element, DropDownMenu menu) {
            return new NumberElement<>(element, menu, name, range, width);
        }
    }
}