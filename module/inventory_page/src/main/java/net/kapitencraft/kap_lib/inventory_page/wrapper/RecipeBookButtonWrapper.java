package net.kapitencraft.kap_lib.inventory_page.wrapper;

import net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class RecipeBookButtonWrapper extends ImageButton {
    private final InventoryPageReader reader;

    public RecipeBookButtonWrapper(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress, InventoryPageReader reader) {
        super(x, y, width, height, sprites, onPress);
        this.reader = reader;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (reader.getPageIndex() == 0) super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}