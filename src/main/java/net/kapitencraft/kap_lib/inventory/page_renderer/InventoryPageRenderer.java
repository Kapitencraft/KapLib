package net.kapitencraft.kap_lib.inventory.page_renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * basic inventory page renderer interface.
 * <br>note that this interface works basically like a {@link net.minecraft.client.gui.screens.Screen Screen} and should be treated as one
 */
public interface InventoryPageRenderer {

    /**
     * render this inventory page
     * @param graphics the rendering handler
     * @param minecraft the minecraft instance
     * @param mouseX the X positon of the mouse
     * @param mouseY the Y position of the mouse
     * @param mouseXOld the old X position of the mouse
     * @param mouseYOld the old Y position of the mouse
     * @param leftPos the position of the left border of the inventories background
     * @param topPos the position of the top border of the inventories background
     */
    void render(GuiGraphics graphics, Minecraft minecraft, int mouseX, int mouseY, float mouseXOld, float mouseYOld, int leftPos, int topPos);

    /**
     * called whenever the inventory changes size
     */
    void init(int leftPos, int topPos);

    /**
     * @return the location of the background texture.
     * <br>the texture must be of size 256x256 with the GUI of size 176x166
     */
    @NotNull ResourceLocation pageBackgroundLocation();

    /**
     * @param relativeX x position of the mouse, relative to the left border of the GUI
     * @param relativeY y position of the mouse, relative to the top border of the GUI
     * @param button the button pressed
     * @return whether the click was successfully consumed
     */
    default boolean onMouseClicked(int relativeX, int relativeY, int button) {
        return false;
    }

    /**
     * mouse dragged handler, see mouse clicked handler for more details
     * @see #onMouseClicked(int, int, int)
     */
    default boolean onMouseDragged(int relativeX, int relativeY, int pButton) {
        return false;
    }

    /**
     * mouse released handler, see mouse clicked handler for more details
     * @see #onMouseClicked(int, int, int)
     */
    default boolean onMouseReleased(int relativeX, int relativeY, int pButton) {
        return false;
    }


    /**
     * @param relativeX x position of the mouse, relative to the left border of the GUI
     * @param relativeY y position of the mouse, relative to the top border of the GUI
     * @param pDelta amount of scrolling
     * @return whether the scroll was successfully consumed
     */
    default boolean onMouseScrolled(int relativeX, int relativeY, double pDelta) {
        return false;
    }
}
