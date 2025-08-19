package net.kapitencraft.kap_lib.client.gui.screen.tooltip;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * add Instances of this into {@link net.kapitencraft.kap_lib.client.gui.screen.IModScreen#addHoverTooltip(HoverTooltip) IModScreen#addHoverTooltip} to make its text shown inside the specified space
 */
public class HoverTooltip {
    private final int xOffsetStart;
    private final int yOffsetStart;
    private final int xSize;
    private final int ySize;
    protected List<Component> text;

    /**
     * @param xOffsetStart the left position, relative to the screen's background, of the rectangle that determines its position
     * @param yOffsetStart the top position, relative to the screen's background, of the rectangle that determines its position
     * @param xSize the width of the rectangle
     * @param ySize the height of the rectangle
     * @param text the text to show if the cursor touches the rectangle
     */
    public HoverTooltip(int xOffsetStart, int yOffsetStart, int xSize, int ySize, List<Component> text) {
        this.xOffsetStart = xOffsetStart;
        this.yOffsetStart = yOffsetStart;
        this.xSize = xSize;
        this.ySize = ySize;
        this.text = text;
    }

    /**
     * @param xPos the left position of the screen's background
     * @param yPos the top position of the screen's background
     * @param xMousePos the mouse x position
     * @param yMousePos the mouse y position
     * @return whether the mouse hovers this Tooltip
     */
    public boolean hovered(int xPos, int yPos, int xMousePos, int yMousePos) {
        return MathHelper.is2dBetween(xMousePos, yMousePos, xPos + this.xOffsetStart, yPos + this.yOffsetStart, xPos + xOffsetStart + xSize, yPos + yOffsetStart + ySize);
    }

    public ImageButton createButton(ResourceLocation imageLocation, int leftPos, int topPos, Button.OnPress task) {
        return new ImageButton(leftPos + xOffsetStart, topPos + yOffsetStart, 16, 16, 0, 0, 16, imageLocation, 16, 16, task);
    }

    public List<Component> getText() {
        return text;
    }
}
