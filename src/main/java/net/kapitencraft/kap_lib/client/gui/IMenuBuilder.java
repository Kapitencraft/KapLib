package net.kapitencraft.kap_lib.client.gui;

import net.kapitencraft.kap_lib.client.gui.screen.MenuableScreen;
import net.kapitencraft.kap_lib.client.widget.menu.Menu;

/**
 * interface, making target able to create menus (use within {@link net.minecraft.client.gui.components.events.GuiEventListener GuiEventListeners})
 * <br>should also only be used inside {@link net.kapitencraft.kap_lib.client.gui.screen.MenuableScreen MenuableScreens}
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
