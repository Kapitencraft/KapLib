package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.client.widget.menu.IValueModifierElement;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BooleanElement extends Element implements IValueModifierElement<BooleanElement, Boolean> {

    private final Consumer<Boolean> onChange;
    private boolean selected = false;
    protected BooleanElement(ListElement parent, DropDownMenu menu, Component name, Consumer<Boolean> onChange) {
        super(parent, menu, name);
        this.onChange = onChange;
    }

    public boolean selected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.onChange.accept(this.selected);
    }

    @Override
    protected int width() {
        return super.width() + 9;
    }

    @Override
    public void click(float relativeX, float relativeY) {
        this.setSelected(!this.selected());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (selected()) {
            int checkBoxX = x + this.effectiveWidth() - 9;
            int checkBoxY = y + 1;
            UsefulTextures.renderCheckMark(graphics, checkBoxX, checkBoxY);
        }
    }

    @Override
    public void setValue(Boolean value) {
        this.selected = value;
    }
}