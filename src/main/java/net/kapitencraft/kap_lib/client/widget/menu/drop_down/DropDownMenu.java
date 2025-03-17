package net.kapitencraft.kap_lib.client.widget.menu.drop_down;

import net.kapitencraft.kap_lib.client.overlay.ConfigureOverlaysScreen;
import net.kapitencraft.kap_lib.client.widget.menu.Menu;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.ListElement;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * drop down menu
 * see {@link ConfigureOverlaysScreen ChangeOverlayLocationsScreen} for example
 */
public class DropDownMenu extends Menu {

    private final ListElement root = new ListElement(null, this, Component.empty()) {
        @Override
        protected boolean shouldShowChildren() {
            return true;
        }
    };

    public DropDownMenu(int x, int y, GuiEventListener listener) {
        super(x, y, listener);
    }

    @Override
    public void show() {
        root.show(x, y);
        root.showChildren();
    }


    @Override
    public void hide(Screen screen) {
        super.hide(screen);
    }

    public void addElement(Element.Builder<?, ?> builder) {
        this.root.addElement(builder.build(this.root, this));
    }

    @Override
    public void render(@NotNull GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        root.renderWithBackground(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (pMouseX < this.x || pMouseY < this.y) return;
        this.root.mouseMove(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            return this.root.mouseClick(pMouseX, pMouseY);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}