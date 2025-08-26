package net.kapitencraft.kap_lib.client.widget;

import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ScrollableWidget extends AbstractWidget {
    public ScrollableWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    protected float scrollX, scrollY;

    protected abstract void updateScroll(boolean ignoreCursor);

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        float scrollOffset = (float) (ClientModConfig.getScrollScale() * pDelta);
        if (canScroll(false)) {
            this.scrollY += scrollOffset;
        } else if (canScroll(true) && Screen.hasControlDown()) {
            this.scrollX += scrollOffset;
        } else {
            return super.mouseScrolled(pMouseX, pMouseY, pDelta);
        }
        updateScroll(true);
        return true;
    }

    protected abstract int valueSize(boolean x);

    protected boolean canScroll(boolean x) {
        return valueSize(x) > (x ? this.width : this.height);
    }
}