package net.kapitencraft.kap_lib.inventory.wrapper;

import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class RecipeBookButtonWrapper extends ImageButton {
    private final InventoryPageReader reader;

    public RecipeBookButtonWrapper(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, OnPress pOnPress, InventoryPageReader reader) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress);
        this.reader = reader;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (reader.getPageIndex() == 0) super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
