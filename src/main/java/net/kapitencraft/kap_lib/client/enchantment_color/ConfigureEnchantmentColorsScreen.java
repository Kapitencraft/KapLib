package net.kapitencraft.kap_lib.client.enchantment_color;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.client.widget.select.ByNameRegistryElementSelectorWidget;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ConfigureEnchantmentColorsScreen extends Screen {
    private static final int ELEMENT_HEIGHT = 70,
            WIDTH = 404,
            HEIGHT = 186,
            SLIDER_WIDTH = 6;

    private static final MutableComponent ADD = Component.literal("+");
    private static final MutableComponent ADD_HOVERED = Component.literal("+").withStyle(ChatFormatting.BOLD);
    private static final Component BOLT = Component.translatable("style.bold").withStyle(ChatFormatting.BOLD);
    private static final Component UNDERLINED = Component.translatable("style.underlined").withStyle(ChatFormatting.UNDERLINE);

    private final EnchantmentColorManager manager;
    private final List<ColorElement> elements = new ArrayList<>();
    private int maxScroll;
    private float scrollY;
    private boolean scrolling;
    private int leftPos, topPos;
    private ColorElement active;
    private ByNameRegistryElementSelectorWidget<Enchantment> enchantmentSelector;

    protected ConfigureEnchantmentColorsScreen() {
        super(Component.translatable("configure_enchantment_colors.title"));
        this.manager = EnchantmentColorManager.instance;
        List<EnchantmentColor> colors = manager.getAllColors();
        colors.stream().map(ColorElement::new).forEach(this.elements::add);
        recalculateScroll();
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;
        this.enchantmentSelector = new ByNameRegistryElementSelectorWidget<>(this.leftPos + 90, this.topPos + 20, WIDTH - 180, HEIGHT - 40, Component.translatable("cec.select_enchantment"), this.font, ForgeRegistries.ENCHANTMENTS, Enchantment::getDescriptionId, enchantment -> {
            if (this.active == null) KapLibMod.LOGGER.warn("unexpected enchantment select with no selected color!");
            else {
                this.active.color.addEnchantment(enchantment);
            }
        });
        super.init();
    }

    private void recalculateScroll() {
        this.maxScroll = Math.max(0, (ELEMENT_HEIGHT + 2) * this.elements.size() - (HEIGHT - 14) + 8);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.leftPos, this.topPos, this.leftPos + WIDTH, this.topPos + HEIGHT, 0xFF404040);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.topPos + 2, 0xFFFFFF);
        pGuiGraphics.fill(this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2, 0xFF505050);
        pGuiGraphics.enableScissor(this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2);
        for (int i = 0; i < this.elements.size(); i++) {
            double mouseRelativeX = pMouseX - this.leftPos - 2;
            double mouseRelativeY = pMouseY - (this.topPos + 12) - i * (ELEMENT_HEIGHT + 2) - scrollY;
            this.elements.get(i).render(pGuiGraphics, (ELEMENT_HEIGHT + 2) * i + (int) scrollY + 12, pPartialTick, mouseRelativeX, mouseRelativeY, getHoveredIndex(pMouseY) == i);
        }
        pGuiGraphics.drawCenteredString(this.font, isAddElementHovered(pMouseX, pMouseY) ? ADD_HOVERED : ADD, this.width / 2, (ELEMENT_HEIGHT + 2) * this.elements.size() + (int) scrollY + 12 + this.topPos, 0xFFFFFF);
        pGuiGraphics.disableScissor();
        if (shouldShowSlider(pMouseX, pMouseY)) {
            float percentage = -scrollY / maxScroll;
            UsefulTextures.renderSliderWithLine(pGuiGraphics, SLIDER_WIDTH, scrolling, percentage, this.leftPos + WIDTH - 2, this.topPos + 12, HEIGHT - 14);
        }
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 100);
        this.enchantmentSelector.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
    }

    private boolean shouldShowSlider(double pMouseX, double pMouseY) {
        return scrolling || maxScroll > 0 && MathHelper.is2dBetween(pMouseX, pMouseY, this.leftPos + WIDTH - 2 - SLIDER_WIDTH, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2);
    }

    private boolean sliderHovered(double pMouseY) {
        if (enchantmentSelector.isVisible()) return false;
        float positionY = this.topPos + 12 + -scrollY * (HEIGHT - 21.5f) / maxScroll;
        return MathHelper.isBetween(pMouseY, positionY, positionY + 7.5);
    }

    private boolean isAddElementHovered(double pMouseX, double pMouseY) {
        if (enchantmentSelector.isVisible()) return false;
        int yPos = (ELEMENT_HEIGHT + 2) * this.elements.size() + (int) scrollY + 12 + this.topPos;
        return MathHelper.is2dBetween(pMouseX, pMouseY, this.width / 2 - 4, yPos, this.width / 2 + 4, yPos + 8);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (shouldShowSlider(pMouseX, pMouseY)) {
            if (sliderHovered(pMouseY)) {
                scrolling = true;
            } else {
                double targetYPos = pMouseY - 3.75;
                //targetYPos = this.topPos + 12 + -scrollY * (HEIGHT - 21.5f) / maxScroll
                scrollY = (float) ((this.topPos + 12 - targetYPos) * maxScroll / (HEIGHT - 21.5f));
                scrollY = Mth.clamp(scrollY, -maxScroll, 0);
            }
            return true;
        }
        if (enchantmentSelector.isVisible() && enchantmentSelector.mouseClicked(pMouseX, pMouseY, pButton))
            return true;
        int index = getHoveredIndex((int) pMouseY);
        if (index != -1) {
            ColorElement hovered = elements.get(index);
            hovered.mouseClicked(pMouseX - this.leftPos - 2, pMouseY - (this.topPos + 12) - index * (ELEMENT_HEIGHT + 2) - scrollY, pButton);
            active = hovered;
            return true;
        }
        if (isAddElementHovered(pMouseX, pMouseY)) {
            this.elements.add(new ColorElement(
                    new EnchantmentColor(
                            I18n.get("enchantment_colors.by_id", this.elements.size()),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            null,
                            Style.EMPTY
                    )
            ));
            this.recalculateScroll();
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (!this.enchantmentSelector.isVisible()) {
            active = null;
            scrolling = false;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (enchantmentSelector.isVisible()) return enchantmentSelector.mouseScrolled(pMouseX, pMouseY, pDelta);
        if (MathHelper.is2dBetween(pMouseX, pMouseY, this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2)) {
            scrollY += (int) (pDelta * ClientModConfig.getScrollScale());
            scrollY = Mth.clamp(scrollY, -maxScroll, 0);
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling) {
            this.scrollY -= (float) (maxScroll * (pDragY / (HEIGHT - 14)));
            this.scrollY = Mth.clamp(scrollY, -maxScroll, 0);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private ColorElement getForYPos(int y) {
        int hovered = getHoveredIndex(y);
        return hovered == -1 ? null : elements.get(hovered);
    }

    private int getHoveredIndex(int y) {
        if (enchantmentSelector.isVisible()) return -1;
        //y = this.topPos + (ELEMENT_HEIGHT + 2) * index + (int) scrollY + 12
        int index = (topPos + (int) scrollY + 12 - y) / - (ELEMENT_HEIGHT + 2);
        return index < 0 || index >= this.elements.size() ? -1 : index;
    }

    private class ColorElement {
        private final EnchantmentColor color;

        private ColorElement(EnchantmentColor color) {
            this.color = color;
        }

        public void render(GuiGraphics guiGraphics, int yOffset, float pPartialTick, double mouseRelativeX, double mouseRelativeY, boolean hovered) {
            int yPos = topPos + yOffset;
            guiGraphics.fill(leftPos + 3, yPos, leftPos + WIDTH - 3, yPos + ELEMENT_HEIGHT, hovered ? 0xFF919191 : 0xFF878787);

            guiGraphics.drawString(font, color.getName(), leftPos + 7, yPos + 1, 0xFFFFFF);

            //Enchantments
            guiGraphics.fill(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60);
            for (int i = 0; i < color.getElements().size(); i++) {
                Enchantment enchantment = color.getElements().get(i);
                guiGraphics.drawString(font, Component.translatable(enchantment.getDescriptionId()), leftPos + 6, yPos + 11 + i * 10, 0xFFFFFF);
                boolean enchantmentHovered = MathHelper.is2dBetween(mouseRelativeX, mouseRelativeY, 5, 11 + i * 10, 80, 21 + i * 10);
                if (enchantmentHovered) {
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 100);
                    UsefulTextures.renderCross(guiGraphics, leftPos + 71, yPos + 11 + i * 10, 8);
                    guiGraphics.pose().popPose();
                }
            }
            guiGraphics.drawString(font, isEnchantmentAddHovered(mouseRelativeX, mouseRelativeY) ? ADD_HOVERED : ADD, leftPos + 6, yPos + 11 + color.getElements().size() * 10, 0xFFFFFF);
            guiGraphics.disableScissor();

            //Groups
            guiGraphics.fill(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60);
            int groupCount = color.getGroups().size();
            for (int i = 0; i < groupCount; i++) {
                EnchantmentGroup group = color.getGroups().get(i);
                guiGraphics.drawString(font, group.getName(), leftPos + 82, yPos + 11 + i * 10, 0xFFFFFF);
                boolean groupHovered = MathHelper.is2dBetween(mouseRelativeX, mouseRelativeY, 81, 11 + i * 10, 161, 21 + i * 10);
                if (groupHovered) {
                    UsefulTextures.renderCross(guiGraphics, leftPos + 152, yPos + 11 + i * 10, 8);
                }
            }
            if (groupCount < EnchantmentGroup.values().length) {
                guiGraphics.drawString(font, isGroupAddHovered(mouseRelativeX, mouseRelativeY) ? ADD_HOVERED : ADD, leftPos + 82, yPos + 11 + groupCount * 10, 0xFFFFFF);
            }
            guiGraphics.disableScissor();

            //Level Check
            boolean levelCheckActive = color.getLevelRange() != null;

            UsefulTextures.renderCheckBox(guiGraphics, leftPos + 165, yPos + 10, 0xFF606060, levelCheckActive);
            guiGraphics.drawString(font, Component.translatable("cec.enable_level"), leftPos + 176, yPos + 10, 0xFF808080);

            UsefulTextures.renderCheckBox(guiGraphics, leftPos + 165, yPos + 22, 0xFF606060, levelCheckActive && color.getLevelRange().isMaxLevelRelative());
            guiGraphics.drawString(font, Component.translatable("cec.relative"), leftPos + 176, yPos + 22, levelCheckActive ? 0xFF808080 : 0xFF404040);

            guiGraphics.drawString(font, Component.translatable("cec.min_level"), leftPos + 165, yPos + 33, levelCheckActive ? 0xFF808080 : 0xFF404040);

            guiGraphics.drawString(font, "<", leftPos + 165, yPos + 44, 0xFF606060);
            guiGraphics.drawString(font, levelCheckActive ? String.valueOf(color.getLevelRange().getMin()) : "0", leftPos + 170, yPos + 44, 0xFF606060);

            guiGraphics.drawString(font, Component.translatable("cec.max_level"), leftPos + 190, yPos + 33, levelCheckActive ? 0xFF808080 : 0xFF404040);

            UsefulTextures.renderCross(guiGraphics, leftPos + WIDTH - 13, yPos + 1, 8);
        }

        public void mouseClicked(double relativeX, double relativeY, int code) {
            if (MathHelper.is2dBetween(relativeX, relativeY, WIDTH - 15, 1, WIDTH - 7, 9)) {
                ConfigureEnchantmentColorsScreen.this.elements.remove(this);
                recalculateScroll();
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 163, 10, 173, 20)) {
                this.color.toggleLevelReq();
            } else if (this.color.getLevelRange() != null) {
                if (MathHelper.is2dBetween(relativeX, relativeY, 163, 22, 173, 32)) {
                    LevelRange current = this.color().getLevelRange();
                    this.color.setLevelRange(new LevelRange(current.getMin(), current.getMax(), !current.isMaxLevelRelative()));
                }
            } else if (isEnchantmentAddHovered(relativeX, relativeY)) {
                active = this;
                enchantmentSelector.setVisible(true);
            } else if (isGroupAddHovered(relativeX, relativeY)) {

            } else if (MathHelper.is2dBetween(relativeX, relativeY, 70, 10, 80, 60)) {
                int id = (int) (relativeY / 10);
                this.color.removeEnchantment(id);
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 81, 10, 161, 60)) {
                int id = (int) (relativeY / 10);
                this.color.removeGroup(id);
            }
        }

        private boolean isEnchantmentAddHovered(double relativeX, double relativeY) {
            if (enchantmentSelector.isVisible()) return false;
            int enchantmentAddY = 11 + color.getElements().size() * 10;
            return MathHelper.is2dBetween(relativeX, relativeY, 4, enchantmentAddY, 12, enchantmentAddY + 8);
        }

        private boolean isGroupAddHovered(double relativeX, double relativeY) {
            if (enchantmentSelector.isVisible()) return false;
            int groupCount = color.getGroups().size();
            return groupCount < EnchantmentGroup.values().length &&
                    MathHelper.is2dBetween(relativeX, relativeY, 80, 11 + groupCount * 10, 88, 19 + groupCount * 10);
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

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            if (this.enchantmentSelector.isVisible()) {
                this.enchantmentSelector.setVisible(false);
            } else this.onClose();
            return true;
        }
        return false;
    }
}
