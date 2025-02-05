package net.kapitencraft.kap_lib.client.widget.select;

import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.client.widget.Widget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class MultiElementSelectorWidget<K> extends PositionedWidget {
    private static final int SLIDER_WIDTH = 6;
    protected K active = null;
    protected final List<ElementButton> buttons = new ArrayList<>();
    protected int allElementsSize;
    private int scrollOffset;
    private boolean scrolling;

    /**
     * @param width the width of the widget in elements, <i>not</i> pixels
     * @param elementSize the width of each element in pixels
     */
    public MultiElementSelectorWidget(int x, int y, int width, int elementSize, int height, List<K> elements) {
        super(x, y, width * elementSize, height);
        int widthAddition = 0;
        int heightAddition = 0;
        for (K element : elements) {
            int xStart = x + elementSize * widthAddition;
            int yStart = y + elementSize * heightAddition;
            createElement(buttons::add, xStart, yStart, elementSize, element);
            if (widthAddition++ >= width - 1) {
                heightAddition++; widthAddition = 0;
            }
        }
        allElementsSize = elements.size() * elementSize;
    }

    protected abstract void createElement(Consumer<ElementButton> adder, int xStart, int yStart, int elementSize, K element);

    private boolean canScroll() {
        return this.allElementsSize > this.height;
    }

    private boolean shouldShowSlider(double pMouseX, double pMouseY) {
        return canScroll() && MathHelper.is2dBetween(pMouseX, pMouseY, this.getMaxX() - SLIDER_WIDTH, this.y, this.getMaxX(), this.getMaxY());
    }

    /**
     * renders inside the view box
     * <br> keep in mind that all render location values are relative to this.x and this.y
     */
    protected void renderInternal(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(- 1, - 1, this.width + 1, this.height + 1, 0xFF404040);
        this.buttons.stream().filter(ElementButton::isShown).forEach(coloredButton -> coloredButton.render(graphics, pMouseX, pMouseY, pPartialTick));
    }

    /**
     * only override when rendering other things than the Selector
     * <br> call super in any way, otherwise override other class
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        this.renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.disableScissor();
        if (shouldShowSlider(pMouseX, pMouseY)) {
            renderSlider(graphics, pMouseX, pMouseY);
        }
    }

    private void renderSlider(GuiGraphics graphics, int pMouseX, int pMouseY) {
        float movePercent = (float) this.scrollOffset / this.allElementsSize;
        boolean flag = MathHelper.is2dBetween(pMouseX, pMouseY, this.getMaxX() - SLIDER_WIDTH, this.y, this.getMaxX(), this.getMaxY());
        UsefulTextures.renderSliderWithLine(graphics, 6, flag, movePercent, this.getMaxX(), 0, this.height);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.hovered(pMouseX, pMouseY)) {
            this.scrollOffset += pDelta;
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (shouldShowSlider(pMouseX, pMouseY)) {
            this.scrolling = true;
            return true;
        }
        return new ArrayList<>(this.buttons) //necessary to avoid ConcurrentModificationException
                .stream().filter(ElementButton::isShown).anyMatch(coloredButton -> coloredButton.mouseClicked(pMouseX, pMouseY, pButton));
    }

    protected class ElementButton extends Widget {
        protected final K own;
        private final int color;
        protected int y;
        protected final int x, size;

        protected ElementButton(int x, int y, int size, K own, int color) {
            this.x = x; this.y = y;
            this.size = size;
            this.own = own;
            this.color = color;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            boolean flag = MultiElementSelectorWidget.this.active == this.own;
            int xOffset = this.getXOffset();
            int yOffset = this.getYOffset();
            if (flag) {
                graphics.fill(xOffset, yOffset, xOffset + this.size, yOffset + this.size, 0xFFD4D4D4);
            }
            graphics.fill(flag ? xOffset + 1 : xOffset, flag ? yOffset + 1 : yOffset, xOffset + this.size - (flag ? 1 : 0),  yOffset + this.size - (flag ? 1 : 0), color);
        }

        
        protected int getXOffset() {
            return this.x - MultiElementSelectorWidget.this.x;
        }

        protected int getYOffset() {
            return this.y - MultiElementSelectorWidget.this.y;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (hovered(pMouseX, pMouseY)) {
                MultiElementSelectorWidget.this.active = this.own;
                return true;
            }
            return false;
        }


        /**
         * @return whether the mouse hovers over this Widget
         */
        protected final boolean hovered(double pMouseX, double pMouseY) {
            return pMouseY < MultiElementSelectorWidget.this.getMaxY() && pMouseY > MultiElementSelectorWidget.this.y && MathHelper.is2dBetween(pMouseX, pMouseY, this.x, this.y, this.x + this.size, this.y + this.size);
        }

        public void move(int yRange) {
            y += yRange;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "{own=" + this.own + ", x=" + this.x + ", y=" + this.y + "}";
        }

        public boolean isShown() {
            MultiElementSelectorWidget<K> selectorWidget = MultiElementSelectorWidget.this;
            return this.y < selectorWidget.getMaxY() && this.y >= selectorWidget.y;
        }
    }
}
