package net.kapitencraft.kap_lib.inventory.page.equipment;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderer;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EquipmentPageRenderer implements InventoryPageRenderer {
    private static final ResourceLocation BACKGROUND = KapLibMod.res("textures/gui/inventory/equipment.png");

    public EquipmentPageRenderer(EquipmentPage page) {
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void render(GuiGraphics graphics, Minecraft minecraft, int mouseX, int mouseY, float mouseXOld, float mouseYOld, int leftPos, int topPos) {
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, leftPos + 88, topPos + 75, 30, (float)(leftPos + 88) - mouseXOld, (float)(topPos + 25) - mouseYOld, minecraft.player);
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
