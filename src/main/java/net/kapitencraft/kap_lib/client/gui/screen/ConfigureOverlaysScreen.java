package net.kapitencraft.kap_lib.client.gui.screen;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.overlay.box.InteractiveBox;
import net.kapitencraft.kap_lib.client.overlay.box.ResizeBox;
import net.kapitencraft.kap_lib.client.overlay.holder.Overlay;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.MultiElementSelectorElement;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * change GUI locations screen
 */
public class ConfigureOverlaysScreen extends MenuableScreen {
    private final OverlayManager controller = LibClient.controller;
    private final List<ResizeBox> boxes = new ArrayList<>();
    private ResizeBox active;
    private final Map<Overlay, ResizeBox> boxesForOverlay = new HashMap<>();

    public ConfigureOverlaysScreen() {
        super(Component.translatable("configure_overlays.title"));
        this.setDefaultMenuBuilder((x, y, screen) -> {
            DropDownMenu menu = new DropDownMenu(x, y, this);
            menu.addElement(
                    MultiElementSelectorElement.builder(Overlay.class)
                            .setName(Component.translatable("gui.overlays"))
                            .setElements(controller.map.values())
                            .setStatusMapper(Overlay::isVisible)
                            .setNameMapper(Overlay::getName)
                            .setOnChange((overlay, aBoolean) -> {
                                OverlayManager.setVisible(overlay, aBoolean);
                                if (aBoolean) {
                                    ResizeBox box = overlay.newBox(width, height, Minecraft.getInstance().player, font);
                                    boxes.add(box);
                                    boxesForOverlay.put(overlay, box);
                                    overlay.setVisible(true);
                                } else {
                                    removeOverlay(overlay);
                                }
                            })
            );
            return menu;
        });
    }

    public void removeOverlay(Overlay overlay) {
        this.boxes.remove(boxesForOverlay.get(overlay));
        overlay.setVisible(false);
    }

    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        boxes.clear();
        controller.fillRenderBoxes(boxes::add, boxesForOverlay::put, player, font, width, height);
        super.init();
    }

    @Override
    public @NotNull List<ResizeBox> children() {
        return boxes;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return active != null && active.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double x, double y, int pButton) {
        if (active == null) return false;
        active.mouseRelease(x, y);
        active = null;
        ClientHelper.changeCursorType(GLFW.GLFW_ARROW_CURSOR);
        return true;
    }

    private List<ResizeBox> getHovering(double x, double y) {
        return boxes.stream().filter(interactiveBox -> interactiveBox.isMouseOver(x, y)).toList();
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        getHovering(x, y).forEach(interactiveBox -> interactiveBox.mouseMove(x, y));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
            if (this.getFocused() instanceof ResizeBox rBox) {
                this.active = rBox;
                int arrowId = rBox.getActiveArrowId();
                ClientHelper.changeCursorType(arrowId);
            }
            return true;
        }
        return false;
    }

    /**
     * renders the screen and updates the Cursor Visuals to match size modifiers
     */
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        boxes.forEach(interactiveBox -> interactiveBox.render(graphics, mouseX, mouseY));
        super.render(graphics, mouseX, mouseY, pPartialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFF4F4F4F);
        if (this.active == null) {
            int arrowId = boxes.stream().map(box -> box.getCursorType(mouseX, mouseY))
                    .filter(i -> i != GLFW.GLFW_ARROW_CURSOR) //ensure to only scan for non-default cursors
                    .findFirst().orElse(GLFW.GLFW_ARROW_CURSOR);
            ClientHelper.changeCursorType(arrowId);
        }
    }

    @Override
    public void onClose() {
        OverlayManager.save();
        super.onClose();
    }
}