package net.kapitencraft.kap_lib.client.widget.select;

import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class SelectRegistryElementWidget<T> extends PositionedWidget {
    private static final Component SELECT_PANEL = Component.translatable("mco.template.button.select");

    private final Component title;
    protected final Font font;
    protected int scrollY;

    protected SelectRegistryElementWidget(int x, int y, int width, int height, Component title, Font font) {
        super(x, y, width, height);
        this.title = title;
        this.font = font;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int widgetMiddle = this.x + this.width / 2;
        pGuiGraphics.drawCenteredString(this.font, this.title, widgetMiddle, this.y + 1, 0xFFFFFF);
        pGuiGraphics.enableScissor(this.x + 1, this.y + 11, getMaxX() - 1, this.getMaxY() - 11);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(this.x + 1, this.y + 11, 0);
        this.renderInternal(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
        pGuiGraphics.disableScissor();
        pGuiGraphics.drawString(this.font, SELECT_PANEL, widgetMiddle, this.getMaxY() - 10, 0xFFFFFF);
    }

    protected abstract void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick);
}
