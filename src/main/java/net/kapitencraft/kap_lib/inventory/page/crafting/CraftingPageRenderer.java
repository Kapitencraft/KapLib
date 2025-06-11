package net.kapitencraft.kap_lib.inventory.page.crafting;

import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CraftingPageRenderer implements InventoryPageRenderer {

    public CraftingPageRenderer(CraftingPage ignored) {
    }

    @Override
    public void render(GuiGraphics graphics, Minecraft minecraft, int mouseX, int mouseY, float mouseXOld, float mouseYOld, int leftPos, int topPos) {
        //EMPTY. done inside InventoryScreen
    }

    @Override
    public void init(int leftPos, int topPos) {

    }

    @Override
    public @NotNull ResourceLocation pageBackgroundLocation() {
        return AbstractContainerScreen.INVENTORY_LOCATION;
    }
}
