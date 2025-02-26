package net.kapitencraft.kap_lib.client.widget.select;

import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SelectRegistryElementWidget<T> extends PositionedWidget {
    private static final Component SELECT_PANEL = Component.translatable("mco.template.button.select");

    private final Component title;
    protected final Font font;
    private final int titleWidth;
    protected int scrollY;
    private boolean visible;
    protected final List<T> allElements;
    protected T selected;
    private final Consumer<T> valueSink;

    protected SelectRegistryElementWidget(int x, int y, int width, int height, Component title, Font font, IForgeRegistry<T> registry, Consumer<T> valueSink) {
        super(x, y, width, height);
        this.allElements = new ArrayList<>();
        for (T element : registry) allElements.add(element);
        this.title = title;
        this.font = font;
        this.valueSink = valueSink;
        titleWidth = this.font.width(SELECT_PANEL);
    }

    @Override
    public final void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!visible) return;
        int widgetMiddle = this.x + this.width / 2;
        pGuiGraphics.fill(this.x, this.y, getMaxX(), getMaxY(), 0xFF090909);
        pGuiGraphics.drawCenteredString(this.font, this.title, widgetMiddle, this.y + 1, 0xFFFFFF);
        pGuiGraphics.enableScissor(this.x + 1, this.y + 11, getMaxX() - 1, this.getMaxY() - 11);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(this.x + 1, this.y + 11, 0);
        this.renderInternal(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
        pGuiGraphics.disableScissor();
        pGuiGraphics.drawString(this.font, SELECT_PANEL, this.getMaxX() - (titleWidth + 2), this.getMaxY() - 10, 0xFFFFFF);
    }

    protected abstract int getHoveredIndex(double pMouseX, double pMouseY);

    protected abstract void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick);

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int widgetMiddle = this.x + this.width / 2;
        int selectPos = this.getMaxX() - (titleWidth + 2);
        if (MathHelper.is2dBetween(pMouseX, pMouseY, selectPos - 1, this.getMaxY() - 10, selectPos + titleWidth + 1, this.getMaxY())) {
            if (this.selected != null) {
                this.valueSink.accept(selected);
                this.selected = null;
                this.setVisible(false);
                return true;
            }
        }
        if (getHoveredIndex(pMouseX, pMouseY) != -1) {
            this.selected = allElements.get(getHoveredIndex(pMouseX, pMouseY));
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected abstract int size();

    public boolean isVisible() {
        return this.visible;
    }
}
