package net.kapitencraft.kap_lib.core.client;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * some useful textures and GUI-rendering helper methods
 */
@OnlyIn(Dist.CLIENT)
public interface UsefulTextures {
    ResourceLocation CHECK_MARK = ResourceLocation.withDefaultNamespace("icon/checkmark");
    ResourceLocation CROSS = LibConstants.res("textures/gui/red_cross.png");
    ResourceLocation SLOT = LibConstants.res("textures/gui/slot_background.png");
    ResourceLocation SLIDER = getGuiLocation("container/loom.png");
    ResourceLocation ARROW_DOWN = ResourceLocation.withDefaultNamespace("transferable_list/move_down");
    ResourceLocation ARROW_DOWN_HIGHLIGHT = ResourceLocation.withDefaultNamespace("transferable_list/move_down_highlighted");
    ResourceLocation ARROW_UP = ResourceLocation.withDefaultNamespace("transferable_list/move_up");
    ResourceLocation ARROW_UP_HIGHLIGHT = ResourceLocation.withDefaultNamespace("transferable_list/move_up_highlighted");

    @SuppressWarnings("SameParameterValue")
    private static ResourceLocation getGuiLocation(String path) {
        return ResourceLocation.withDefaultNamespace("textures/gui/" + path);
    }

    /**
     * renders a check mark at the given coordinates with an unscaled size of 8x8
     * @param graphics the GuiGraphics object
     * @param checkBoxX x coordinate of the checkbox
     * @param checkBoxY y coordinate of the checkbox
     */
    static void renderCheckMark(GuiGraphics graphics, int checkBoxX, int checkBoxY) {
        graphics.blitSprite(CHECK_MARK, 9, 8, 0, 0, checkBoxX, checkBoxY, 9, 8);
    }

    /**
     * renders a red cross at the given coordinate
     * @param graphics the GuiGraphics object
     * @param x x coordinate of the cross
     * @param y y coordinate of the cross
     * @param size what size the cross should be. default is 8x7
     */
    static void renderCross(GuiGraphics graphics, int x, int y, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(size / 8f, size / 7f, 0);
        graphics.blit(CROSS, 0, 0, 0, 0, 0, 8, 7, 8, 7);
        graphics.pose().popPose();
    }

    /**
     * renders a scroll slider with bar
     * @param graphics the GuiGraphics object
     * @param sliderWidth the width of the slider
     * @param selected whether the slider should be rendered as selected or not
     * @param movePercent the percentage of the bar the slider is at on the y-axis, relative to the top
     * @param rightAlignment the right x position of the bar
     * @param minY the x min position
     * @param height the height of the bar
     */
    static void renderSliderWithLine(GuiGraphics graphics, int sliderWidth, boolean selected, float movePercent, int rightAlignment, int minY, int height) {
        graphics.fill(rightAlignment - sliderWidth, minY, rightAlignment, minY + height, 0x2DFFFFFF);
        UsefulTextures.renderSlider(graphics, rightAlignment - sliderWidth, minY + (int) (movePercent * (height - sliderHeightForWidth(sliderWidth))), selected, sliderWidth / 12f);
    }

    @ApiStatus.Internal
    static int sliderHeightForWidth(int width) {
        return 15 * width / 12;
    }

    /**
     * renders a slider without bar at the given coordinates and scale
     * @param light whether the selected or unselected sprite should be used
     */
    static void renderSlider(GuiGraphics graphics, int x, int y, boolean light, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 0);
        graphics.blit(SLIDER, 0, 0, 232 + (light ? 0 : 12), 0, 12, 15);
        graphics.pose().popPose();
    }

    /**
     * @param pLeft the x coordinate of the button
     * @param pTop the y coordinate of the button
     * @param hovered whether the button is hovered (will show different sprite)
     * @param size the size of the button
     */
    static void renderUpButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop, 0);
        float scale = size / 16f;
        graphics.pose().scale(scale, scale, 0);
        if (hovered) {
            graphics.blit(ARROW_UP_HIGHLIGHT, 0, 0, 96, 32, 32, 32);
        } else {
            graphics.blit(ARROW_UP, 0, 0, 96, 0, 32, 32);
        }
        graphics.pose().popPose();
    }

    /**
     * @param graphics the GuiGraphics object
     * @param pLeft the x coordinate of the button
     * @param pTop the y coordinate of the button
     * @param hovered whether the button is hovered (will show different sprite)
     * @param size the size of the button
     */
    static void renderDownButton(GuiGraphics graphics, int pLeft, int pTop, boolean hovered, int size) {
        graphics.pose().pushPose();
        graphics.pose().translate(pLeft, pTop - size, 0);
        graphics.pose().scale(size / 16f, size / 16f, 0);
        if (hovered) {
            graphics.blit(ARROW_DOWN_HIGHLIGHT, 0, 0, 64, 32, 32, 32);
        } else {
            graphics.blit(ARROW_DOWN, 0, 0, 64, 0, 32, 32);
        }
        graphics.pose().popPose();
    }

    /**
     * @param graphics the GuiGraphics object
     * @param x the x coordinate of the checkbox
     * @param y the y coordinate of the checkbox
     * @param backgroundColor the background color of the checkbox
     * @param active whether the checkbox is checked or not
     */
    static void renderCheckBox(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active) {
        graphics.fill(x - 1, y - 1, x + 9, y + 9, backgroundColor);
        if (active) renderCheckMark(graphics, x, y);
    }

    /**
     * renders a checkbox and the given text behind it
     * @param graphics the GuiGraphics object
     * @param x x coordinate of the checkbox
     * @param y y coordinate of the checkbox
     * @param backgroundColor the background color of the checkbox
     * @param active whether the checkbox is checked or not
     * @param font the font to use for the text
     * @param textColor the color for the text
     * @param text the text
     */
    static void renderCheckBoxWithText(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active, Font font, int textColor, Component text) {
        renderCheckBox(graphics, x, y, backgroundColor, active);
        graphics.drawString(font, text, x + 11, y, textColor);
    }

    /**
     * @param dropShadow whether to render text shadow
     * @see #renderCheckBoxWithText(GuiGraphics, int, int, int, boolean, Font, int, Component)
     */
    static void renderCheckBoxWithText(GuiGraphics graphics, int x, int y, int backgroundColor, boolean active, Font font, int textColor, Component text, boolean dropShadow) {
        renderCheckBox(graphics, x, y, backgroundColor, active);
        graphics.drawString(font, text, x + 11, y, textColor, dropShadow);
    }

    /**
     * note that slots are put relative to the top left of the container background image, not the top left of the screen, which this method requires
     * @param slotY the y position of the slot
     * @param slotX the x position of the slot.
     */
    static void renderSlotBackground(GuiGraphics graphics, int slotX, int slotY) {
        graphics.blit(SLOT, slotX - 1, slotY - 1, 0, 0, 18, 18, 18, 18);
    }
}