package net.kapitencraft.kap_lib.client.gui.screen;

import net.kapitencraft.kap_lib.client.gui.IMenuBuilder;
import net.kapitencraft.kap_lib.client.widget.menu.Menu;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * screen with ability to show menus (like {@link net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu DropDownMenus})
 */
public class MenuableScreen extends Screen {

    /**
     * the current active rendered Menu
     */
    private Menu active;
    private IMenuBuilder defaultMenuBuilder;
    protected MenuableScreen(Component pTitle) {
        super(pTitle);
    }

    protected void setDefaultMenuBuilder(IMenuBuilder defaultMenuBuilder) {
        this.defaultMenuBuilder = defaultMenuBuilder;
    }

    @Override
    public void render(@NotNull GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        MiscHelper.ifNonNull(this.active, menu -> menu.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        MiscHelper.ifNonNull(this.active, menu -> menu.mouseMoved(pMouseX, pMouseY));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        GuiEventListener listener = null;

        if (this.getFocused() instanceof Menu && this.getFocused().mouseClicked(pMouseX, pMouseY, pButton)) {
            listener = this.getFocused();
        } else for (GuiEventListener eventListener : List.copyOf(this.children())) {
            if (eventListener.mouseClicked(pMouseX, pMouseY, pButton)) {
                if (eventListener instanceof IMenuBuilder builder && pButton == 1) {
                    listener = makeMenu(builder, pMouseX, pMouseY);
                } else {
                    listener = eventListener;
                }
            }
        }
        if (pButton == 1 && listener == null && this.defaultMenuBuilder != null) {
            listener = makeMenu(defaultMenuBuilder, pMouseX, pMouseY);
        }
        if (listener != null) {
            this.setFocused(listener);
            if (pButton == 0) {
                this.setDragging(true);
            }

            return true;
        } else if (this.active != null) {
            this.closeMenu();
            return true;
        }
        return false;
    }

    private GuiEventListener makeMenu(IMenuBuilder builder, double pMouseX, double pMouseY) {
        Menu menu = builder.createMenu(Mth.floor(pMouseX), Mth.floor(pMouseY), this);
        if (menu != null) {
            this.active = menu;
            this.active.show();
            return this.active;
        }
        return null;
    }

    public void closeMenu() {
        this.active.hide(this);
        this.active = null;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256 && this.active != null) {
            closeMenu();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener pListener) {
        super.setFocused(pListener == this ? null : pListener);
    }
}
