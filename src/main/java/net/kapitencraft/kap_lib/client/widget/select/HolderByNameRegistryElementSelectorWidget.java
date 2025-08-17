package net.kapitencraft.kap_lib.client.widget.select;

import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class HolderByNameRegistryElementSelectorWidget<T> extends PositionedWidget {
    private static final Component SELECT_PANEL = Component.translatable("mco.template.button.select");

    private final Component title;
    protected final Font font;
    private final int titleWidth;
    protected float scroll;
    protected boolean scrolling;
    protected int maxScroll;
    protected final List<Holder<T>> allElements;
    protected Holder<T> selected;
    private final Consumer<Holder<T>> valueSink;
    private final Function<Holder<T>, String> keyExtractor;

    public HolderByNameRegistryElementSelectorWidget(int x, int y, int width, int height, Component title, Font font, Registry<T> registry, Consumer<Holder<T>> valueSink, Function<Holder<T>, String> keyExtractor) {
        super(x, y, width, height);
        this.keyExtractor = keyExtractor;
        this.allElements = new ArrayList<>();
        registry.holders().forEach(allElements::add);
        this.title = title;
        this.font = font;
        this.valueSink = valueSink;
        titleWidth = this.font.width(SELECT_PANEL);
        this.maxScroll = size() - (height - 22);
        this.scroll = 0;
    }

    @Override
    public final void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int widgetMiddle = this.x + this.width / 2;
        pGuiGraphics.fill(this.x, this.y, getMaxX(), getMaxY(), 0xFF090909);
        pGuiGraphics.drawCenteredString(this.font, this.title, widgetMiddle, this.y + 1, 0xFFFFFF);
        pGuiGraphics.enableScissor(this.x + 1, this.y + 11, getMaxX() - 1, this.getMaxY() - 11);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(this.x + 1, this.y + 11, 0);
        this.renderInternal(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
        UsefulTextures.renderSliderWithLine(pGuiGraphics, 4, true, this.scroll / -maxScroll, getMaxX() - 1, this.y + 11, height - 22);
        pGuiGraphics.disableScissor();
        pGuiGraphics.drawString(this.font, SELECT_PANEL, this.getMaxX() - (titleWidth + 2), this.getMaxY() - 10, 0xFFFFFF);
    }

    protected int getHoveredIndex(double pMouseX, double pMouseY) {
        //yPos = this.y + this.scrollY + 12 * id - 1;
        return ((int) pMouseY - this.y - (int) this.scroll - 10) / 12;
    }

    protected void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int middleX = this.width / 2 - 1;
        int id = 0;
        //y = this.scrollY + 12 * id + this.y;
        int hoveredIndex = (pMouseY - this.y - (int) this.scroll - 10) / 12;
        for (Holder<T> t : allElements) {
            graphics.fill(1, (int) this.scroll + 12 * id - 1, width - 1, (int) this.scroll + 12 * id + 9, selected == t ? 0xFF404040 : id == hoveredIndex ? 0xFF303030 : 0xFF202020);
            graphics.drawCenteredString(this.font, Component.translatable(keyExtractor.apply(t)), middleX, (int) this.scroll + 12 * id, 0xFFFFFF);
            id++;
        }

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int widgetMiddle = this.x + this.width / 2;
        int selectPos = this.getMaxX() - (titleWidth + 2);
        if (MathHelper.is2dBetween(pMouseX, pMouseY, this.getMaxX() - 7, this.y + 11, this.getMaxX() -1, getMaxY() - 11)) {
            if (sliderHovered(pMouseY)) {
                scrolling = true;
            } else {
                double targetYPos = pMouseY - 2.5;
                scroll = (float) ((this.y + 11 - targetYPos) * maxScroll / (height - 27));
                scroll = Mth.clamp(scroll, -maxScroll, 0);
            }
            return true;
        } else if (MathHelper.is2dBetween(pMouseX, pMouseY, selectPos - 1, this.getMaxY() - 10, selectPos + titleWidth + 1, this.getMaxY())) {
            if (this.selected != null) {
                this.valueSink.accept(selected);
                this.selected = null;
                return true;
            }
        } else if (getHoveredIndex(pMouseX, pMouseY) != -1) {
            this.selected = allElements.get(getHoveredIndex(pMouseX, pMouseY));
            return true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private boolean sliderHovered(double pMouseY) {
        float positionY = y + 11 + -scroll * (height - 27) / maxScroll;
        return MathHelper.isBetween(pMouseY, positionY, positionY + 5);
    }

    protected int size() {
        return this.allElements.size();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        if (MathHelper.is2dBetween(pMouseX, pMouseY, this.x + 1, this.y + 11, getMaxX() - 1, this.getMaxY() - 11)) {
            this.scroll += (float) (scrollY * ClientModConfig.getScrollScale());
            this.scroll = Mth.clamp(scroll, -maxScroll, 0);
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling) {
            this.scroll = (float) (Mth.clamp(pMouseY - (this.y + 11), 0, height - 11) * -maxScroll / (height - 11));
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (scrolling) this.scrolling = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
