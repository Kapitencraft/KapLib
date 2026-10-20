package net.kapitencraft.kap_lib.core.client.menu;

import net.kapitencraft.kap_lib.core.client.menu.widget.Menu;

/**
 * interface, making target able to create menus (use within {@link net.minecraft.client.gui.components.events.GuiEventListener GuiEventListeners})
 * <br>should also only be used inside {@link MenuableScreen MenuableScreens}
 */
@FunctionalInterface
public interface IMenuBuilder {
    /**
     * @param x mouse x
     * @param y mouse y
     * @return the created menu
     */
    Menu createMenu(int x, int y, MenuableScreen screen);
}
