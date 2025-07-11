package net.kapitencraft.kap_lib.inventory.page_renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface InventoryPageRenderer {

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
     * @return whether the click was successfully consumed
     */
    default boolean onMouseClicked(int relativeX, int relativeY) {
        return false;
    }
}
