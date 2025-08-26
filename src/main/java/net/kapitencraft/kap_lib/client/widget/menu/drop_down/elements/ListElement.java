package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ListElement extends Element {
    protected final List<Element> children = new ArrayList<>();
    private boolean showChildren = false;

    public ListElement(@Nullable ListElement listElement, DropDownMenu menu, Component component) {
        super(listElement, menu, component);
    }

    public void addElement(Element element) {
        children.add(element);
    }

    public void showChildren() {
        int newY = y;
        int x = this.x + this.effectiveWidth();
        for (Element element : children) {
            element.show(x, newY);
            newY += OFFSET_PER_ELEMENT;
        }
        showChildren = true;
    }

    public void hideChildren() {
        this.children.forEach(Element::hide);
        showChildren = false;
    }

    protected boolean shouldShowChildren() {
        return showChildren;
    }

    @Override
    public void click(float relativeX, float relativeY) {
        if (this.shouldShowChildren()) this.children.forEach(element -> element.click(relativeX, relativeY));
    }

    public int childrenWidth() {
        return this.children.stream().mapToInt(Element::width).max().orElse(0);
    }

    @Override
    public void startHovering(int x, int y) {
        super.startHovering(x, y);
        this.showChildren();
    }

    @Override
    public void endHovering() {
        super.endHovering();
        this.hideChildren();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (shouldShowChildren()) this.children.forEach(element -> element.renderWithBackground(graphics, pMouseX, pMouseY, pPartialTick));
    }

    @Override
    public void mouseMove(double absoluteX, double absoluteY) {
        if (this.shouldShowChildren()) this.children.forEach(element -> element.mouseMove(absoluteX, absoluteY));
        super.mouseMove(absoluteX, absoluteY);
    }

    protected boolean isChildrenFocused() {
        return this.shouldShowChildren() && this.children.stream().anyMatch(Element::isFocused);
    }

    @Override
    protected boolean isFocused() {
        return super.isFocused() || isChildrenFocused();
    }

    @Override
    public Element getFocused() {
        if (this.isChildrenFocused()) {
            for (Element element : this.children) {
                if (element.isFocused()) {
                    return element.getFocused();
                }
            }
        }
        return super.isFocused() ? this : null;
    }

    public static class Builder extends Element.Builder<ListElement, Builder> {
        private final List<Element> elements = new ArrayList<>();

        public Builder addElement(Element element) {
            elements.add(element);
            return this;
        }

        @Override
        public ListElement build(ListElement element, DropDownMenu menu) {
            ListElement newElement = new ListElement(element, menu, name);
            elements.forEach(newElement::addElement);
            return newElement;
        }
    }
}