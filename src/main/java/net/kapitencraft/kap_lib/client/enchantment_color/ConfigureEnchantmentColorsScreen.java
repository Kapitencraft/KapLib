package net.kapitencraft.kap_lib.client.enchantment_color;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ConfigureEnchantmentColorsScreen extends Screen {
    private static final int ELEMENT_HEIGHT = 70,
            WIDTH = 404,
            HEIGHT = 186,
            SLIDER_WIDTH = 6;

    private final EnchantmentColorManager manager;
    private final List<ColorElement> elements = new ArrayList<>();
    private int maxScroll;
    private int scrollY;
    private boolean scrolling;
    private int leftPos, topPos;
    private ColorElement active;

    protected ConfigureEnchantmentColorsScreen() {
        super(Component.translatable("configure_enchantment_colors.title"));
        this.manager = LibClient.enchantmentColors;
        List<EnchantmentColor> colors = manager.getAllColors();
        colors.stream().map(ColorElement::new).forEach(this.elements::add);
        recalculateScroll();
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;
        super.init();
    }

    private void recalculateScroll() {
        this.maxScroll = Math.max(0, (ELEMENT_HEIGHT + 2) * this.elements.size() - HEIGHT);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.leftPos, this.topPos, this.leftPos + WIDTH, this.topPos + HEIGHT, 0xFF404040);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.topPos + 2, 0xFFFFFF);
        pGuiGraphics.fill(this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2, 0xFF505050);
        pGuiGraphics.enableScissor(this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2);
        for (int i = 0; i < this.elements.size(); i++) {
            this.elements.get(i).render(pGuiGraphics, (ELEMENT_HEIGHT + 2) * i + scrollY + 12, pPartialTick, getHoveredIndex(pMouseY) == i);
        }
        pGuiGraphics.disableScissor();
        if (shouldShowSlider(pMouseX, pMouseY)) {
            float percentage = (float) scrollY / maxScroll;
            UsefulTextures.renderSliderWithLine(pGuiGraphics, SLIDER_WIDTH, true, percentage, this.leftPos + WIDTH - 2, this.topPos + 12, HEIGHT - 14);
        }
    }

    private boolean shouldShowSlider(double pMouseX, double pMouseY) {
        return maxScroll > 0 && MathHelper.is2dBetween(pMouseX, pMouseY, this.leftPos + WIDTH - 2 - SLIDER_WIDTH, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 12);
    }

    private boolean sliderHovered(double pMouseX, double pMouseY) {

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (sliderHovered(pMouseX, pMouseY)) {
            scrolling = true;
            return true;
        }
        ColorElement hovered = getForYPos((int) pMouseY);
        if (hovered != null) {
            hovered.mouseClicked(pMouseX, pMouseY, pButton);
            active = hovered;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        active = null;
        scrolling = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (MathHelper.is2dBetween(pMouseX, pMouseY, this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2)) {
            scrollY -= (int) (pDelta * ClientModConfig.getScrollScale());
            scrollY = Mth.clamp(scrollY, -maxScroll, 0);
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling) {

        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private ColorElement getForYPos(int y) {
        int hovered = getHoveredIndex(y);
        return hovered == -1 ? null : elements.get(hovered);
    }

    private int getHoveredIndex(int y) {
        int index = (y - this.topPos + this.scrollY) / ELEMENT_HEIGHT;
        return index < 0 || index >= this.elements.size() ? -1 : index;
    }

    private class ColorElement {
        private final EnchantmentColor color;

        private ColorElement(EnchantmentColor color) {
            this.color = color;
        }

        public void render(GuiGraphics guiGraphics, int yOffset, float pPartialTick, boolean hovered) {
            int yPos = topPos + yOffset;
            guiGraphics.fill(leftPos + 3, yPos, leftPos + WIDTH - 3, yPos + ELEMENT_HEIGHT, hovered ? 0xFF919191 : 0xFF878787);

            guiGraphics.drawString(font, color.getName(), leftPos + 7, yPos + 1, 0xFFFFFF);
            guiGraphics.fill(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60);
            for (int i = 0; i < color.getElements().size(); i++) {
                Enchantment enchantment = color.getElements().get(i);
                guiGraphics.drawString(font, Component.translatable(enchantment.getDescriptionId()), leftPos + 6, yPos + 11 + (i * 10), 0xFFFFFF);
            }
            guiGraphics.drawString(font, "+", leftPos + 6, yPos + 11 + color.getElements().size() * 10, 0xFFFFFF);
            guiGraphics.disableScissor();

            guiGraphics.fill(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60);
            int groupCount = color.getGroups().size();
            for (int i = 0; i < groupCount; i++) {
                EnchantmentGroup group = color.getGroups().get(i);
                guiGraphics.drawString(font, group.getName(), leftPos + 82, yPos + 11 + i * 10, 0xFFFFFF);
            }
            if (groupCount < EnchantmentGroup.values().length) {
                guiGraphics.drawString(font, "+", leftPos + 82, yPos + 11 + groupCount * 10, 0xFFFFFF);
            }
            guiGraphics.disableScissor();

        }

        public void mouseClicked(double x, double y, int code) {
            if (false) {
                ConfigureEnchantmentColorsScreen.this.elements.remove(this);
            }
        }

        private EnchantmentColor color() {
            return this.color;
        }
    }

    @Override
    public void onClose() {
        this.manager.save(this.elements.stream().map(ColorElement::color).toList());
        super.onClose();
    }
}
