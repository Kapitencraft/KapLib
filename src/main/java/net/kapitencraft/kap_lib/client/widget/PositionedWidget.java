package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.helpers.MathHelper;

public abstract class PositionedWidget extends Widget {
    public final int x, y, width, height;

    protected PositionedWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected boolean hovered(double mouseX, double mouseY) {
        return MathHelper.is2dBetween(mouseX, mouseY, this.x, this.y, this.getMaxX(), this.getMaxY());
    }

    protected int getMaxX() {
        return this.x + this.width;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return hovered(pMouseX, pMouseY);
    }

    protected int getMaxY() {
        return this.y + this.height;
    }
}