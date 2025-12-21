package net.kapitencraft.kap_lib.inventory_page.page.equipment;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.client.UsefulTextures;
import net.kapitencraft.kap_lib.inventory_page.page_renderer.InventoryPageRenderer;
import net.kapitencraft.kap_lib.inventory_page.wearable.Wearables;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EquipmentPageRenderer implements InventoryPageRenderer {
    private static final ResourceLocation BACKGROUND = LibConstants.res("textures/gui/inventory/equipment.png");

    public EquipmentPageRenderer(EquipmentPage page) {
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void render(GuiGraphics graphics, Minecraft minecraft, int mouseX, int mouseY, float mouseXOld, float mouseYOld, int leftPos, int topPos) {
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                leftPos + 63, topPos + 8,
                leftPos + 112, topPos + 78,
                30, 0.0625f,
                mouseX, mouseY, minecraft.player);
        for (WearableSlot slot : Wearables.SLOTS) {
            UsefulTextures.renderSlotBackground(graphics, slot.getXPos() + leftPos, slot.getYPos() + topPos);
        }
    }

    @Override
    public void init(int leftPos, int topPos) {

    }

    @Override
    public @NotNull ResourceLocation pageBackgroundLocation() {
        return BACKGROUND;
    }
}
