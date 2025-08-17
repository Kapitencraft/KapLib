package net.kapitencraft.kap_lib.client.enchantment_color;

import net.kapitencraft.kap_lib.client.UsefulTextures;
import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.client.widget.SelectChatColorWidget;
import net.kapitencraft.kap_lib.client.widget.SelectCountWidget;
import net.kapitencraft.kap_lib.client.widget.SelectEnumWidget;
import net.kapitencraft.kap_lib.client.widget.select.ByNameRegistryElementSelectorWidget;
import net.kapitencraft.kap_lib.client.widget.select.HolderByNameRegistryElementSelectorWidget;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.util.range.simple.IntegerNumberRange;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//TODO fix scale being wrong
public class ConfigureEnchantmentColorsScreen extends Screen {
    private static final int ELEMENT_HEIGHT = 70,
            WIDTH = 404,
            HEIGHT = 186,
            SLIDER_WIDTH = 6;

    private static final MutableComponent ADD = Component.literal("+");
    private static final MutableComponent ADD_HOVERED = Component.literal("+").withStyle(ChatFormatting.BOLD);

    private final EnchantmentColorManager manager;
    private final List<ColorElement> elements = new ArrayList<>();
    private int maxScroll;
    private float scrollY;
    private boolean scrolling;
    private int leftPos, topPos;
    private ColorElement active;
    private @Nullable PositionedWidget selector;

    protected ConfigureEnchantmentColorsScreen() {
        super(Component.translatable("cec.title"));
        this.manager = EnchantmentColorManager.getInstance();
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
        if (this.selector != null) this.selector.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
    }

    private boolean shouldShowSlider(double pMouseX, double pMouseY) {
        return scrolling || maxScroll > 0 && MathHelper.is2dBetween(pMouseX, pMouseY, this.leftPos + WIDTH - 2 - SLIDER_WIDTH, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2);
    }

    private boolean sliderHovered(double pMouseY) {
        float positionY = this.topPos + 12 + -scrollY * (HEIGHT - 21.5f) / maxScroll;
        return MathHelper.isBetween(pMouseY, positionY, positionY + 7.5);
    }

    private boolean isAddElementHovered(double pMouseX, double pMouseY) {
        if (selector != null) return false;
        int yPos = (ELEMENT_HEIGHT + 2) * this.elements.size() + (int) scrollY + 12 + this.topPos;
        return MathHelper.is2dBetween(pMouseX, pMouseY, this.width / 2 - 4, yPos, this.width / 2 + 4, yPos + 8);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (selector != null) return selector.mouseClicked(pMouseX, pMouseY, pButton);

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
        int index = getHoveredIndex((int) pMouseY);
        if (index != -1) {
            ColorElement hovered = elements.get(index);
            hovered.mouseClicked(pMouseX - this.leftPos - 2, pMouseY - (this.topPos + 12) - index * (ELEMENT_HEIGHT + 2) - scrollY, pButton);
            active = hovered;
            return true;
        }
        if (isAddElementHovered(pMouseX, pMouseY)) {
            this.elements.add(new ColorElement(
                    EnchantmentColor.create(
                            I18n.get("enchantment_colors.by_id", this.elements.size() + 1),
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
        if (this.selector == null) {
            active = null;
            scrolling = false;
        } else return this.selector.mouseReleased(pMouseX, pMouseY, pButton);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (selector != null) return selector.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (MathHelper.is2dBetween(mouseX, mouseY, this.leftPos + 2, this.topPos + 12, this.leftPos + WIDTH - 2, this.topPos + HEIGHT - 2)) {
            this.scrollY += (int) (scrollY * ClientModConfig.getScrollScale());
            this.scrollY = Mth.clamp(this.scrollY, -maxScroll, 0);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.selector != null) return selector.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        if (scrolling) {
            this.scrollY = (float) (Mth.clamp(pMouseY - (this.topPos + 12), 0, HEIGHT - 14) * -maxScroll / (HEIGHT - 14));
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private ColorElement getForYPos(int y) {
        int hovered = getHoveredIndex(y);
        return hovered == -1 ? null : elements.get(hovered);
    }

    private int getHoveredIndex(int y) {
        if (selector != null) return -1;
        //y = this.topPos + (ELEMENT_HEIGHT + 2) * index + (int) scrollY + 12
        int index = (topPos + (int) scrollY + 12 - y) / - (ELEMENT_HEIGHT + 2);
        return index < 0 || index >= this.elements.size() ? -1 : index;
    }

    private class ColorElement {
        private String name;
        private SelectChatColorWidget.ColorType colorType;
        private @Nullable LevelRange levelRange;
        private boolean bold, underlined, italic;
        private final List<Holder<Enchantment>> enchantments;
        private final List<EnchantmentGroup> groups;

        private ColorElement(EnchantmentColor color) {
            this.colorType = SelectChatColorWidget.getColor(color);
            Style style = color.targetStyle();
            this.bold = style.isBold();
            this.underlined = style.isUnderlined();
            this.italic = style.isItalic();
            this.levelRange = color.levelRange();
            this.name = color.name();
            this.enchantments = color.elements();
            this.groups = color.groups();
        }

        public void render(GuiGraphics guiGraphics, int yOffset, float pPartialTick, double mouseRelativeX, double mouseRelativeY, boolean hovered) {
            int yPos = topPos + yOffset;
            guiGraphics.fill(leftPos + 3, yPos, leftPos + WIDTH - 3, yPos + ELEMENT_HEIGHT, hovered ? 0xFF919191 : 0xFF888888);

            guiGraphics.drawString(font, Component.literal(name).withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE), leftPos + 7, yPos + 1, 0xFFFFFF);

            //Enchantments
            guiGraphics.fill(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 5, yPos + 10, leftPos + 80, yPos + 60);
            for (int i = 0; i < enchantments.size(); i++) {
                Holder<Enchantment> enchantment = enchantments.get(i);
                guiGraphics.drawString(font, Component.translatable(Util.makeDescriptionId("enchantment", enchantment.getKey().location())), leftPos + 6, yPos + 11 + i * 10, 0xFFFFFF);
                boolean enchantmentHovered = MathHelper.is2dBetween(mouseRelativeX, mouseRelativeY, 5, 11 + i * 10, 80, 21 + i * 10);
                if (enchantmentHovered) {
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 100);
                    UsefulTextures.renderCross(guiGraphics, leftPos + 71, yPos + 11 + i * 10, 8);
                    guiGraphics.pose().popPose();
                }
            }
            guiGraphics.drawString(font, isEnchantmentAddHovered(mouseRelativeX, mouseRelativeY) ? ADD_HOVERED : ADD, leftPos + 6, yPos + 11 + enchantments.size() * 10, 0xFFFFFF);
            guiGraphics.disableScissor();

            //Groups
            guiGraphics.fill(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60, 0xFF404040);
            guiGraphics.enableScissor(leftPos + 81, yPos + 10, leftPos + 161, yPos + 60);
            int groupCount = groups.size();
            for (int i = 0; i < groupCount; i++) {
                EnchantmentGroup group = groups.get(i);
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
            boolean levelCheckActive = levelRange != null;

            UsefulTextures.renderCheckBoxWithText(guiGraphics, leftPos + 165, yPos + 11, 0xFF606060, levelCheckActive,
                    font, 0xFFFFFFFF, Component.translatable("cec.enable_level")
            );

            UsefulTextures.renderCheckBoxWithText(guiGraphics, leftPos + 165, yPos + 23, 0xFF606060, levelCheckActive && levelRange.isMaxLevelRelative(),
                    font, levelCheckActive ? 0xFFFFFFFF : 0xFF404040, Component.translatable("cec.relative"), levelCheckActive
            );

            guiGraphics.drawString(font, Component.translatable("cec.min_level"), leftPos + 165, yPos + 34, levelCheckActive ? 0xFFFFFFFF : 0xFF404040, levelCheckActive);

            guiGraphics.drawString(font, levelCheckActive ? String.valueOf(levelRange.getMin()) : "0", leftPos + 165, yPos + 45, levelCheckActive ? 0xFFFFFFFF : 0xFF404040, levelCheckActive);

            guiGraphics.drawString(font, Component.translatable("cec.max_level"), leftPos + 190, yPos + 34, levelCheckActive ? 0xFFFFFFFF : 0xFF404040, levelCheckActive);

            guiGraphics.drawString(font, levelCheckActive ? String.valueOf(levelRange.getMax()) : "0", leftPos + 190, yPos + 45, levelCheckActive ? 0xFFFFFFFF : 0xFF404040, levelCheckActive);

            UsefulTextures.renderCheckBoxWithText(guiGraphics, leftPos + 280, yPos + 11, 0xFF606060, bold,
                    font, 0xFFFFFFFF, Component.translatable("cec.style.bold").withStyle(ChatFormatting.BOLD)
            );
            UsefulTextures.renderCheckBoxWithText(guiGraphics, leftPos + 280, yPos + 23, 0xFF606060, underlined,
                    font, 0xFFFFFFFF, Component.translatable("cec.style.underlined").withStyle(ChatFormatting.UNDERLINE)
            );
            UsefulTextures.renderCheckBoxWithText(guiGraphics, leftPos + 280, yPos + 35, 0xFF606060, italic,
                    font, 0xFFFFFFFF, Component.translatable("cec.style.italic").withStyle(ChatFormatting.ITALIC)
            );

            this.colorType.render(guiGraphics, leftPos + 279, yPos + 46, 10);
            guiGraphics.drawString(font, Component.translatable("cec.style.color"), leftPos + 291, yPos + 47, 0xFFFFFFF0);

            UsefulTextures.renderCross(guiGraphics, leftPos + WIDTH - 13, yPos + 1, 8);
        }

        public void mouseClicked(double relativeX, double relativeY, int code) {
            if (MathHelper.is2dBetween(relativeX, relativeY, WIDTH - 15, 1, WIDTH - 7, 9)) {
                ConfigureEnchantmentColorsScreen.this.elements.remove(this);
                recalculateScroll();
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 163, 10, 173, 20)) {
                this.toggleLevelReq();
            } else if (isEnchantmentAddHovered(relativeX, relativeY)) {
                active = this;
                selector = new HolderByNameRegistryElementSelectorWidget<>(
                        leftPos + 90,
                        topPos + 20,
                        WIDTH - 180,
                        HEIGHT - 40,
                        Component.translatable("cec.select_enchantment"),
                        font,
                        Minecraft.getInstance().getConnection().registryAccess().registryOrThrow(Registries.ENCHANTMENT),
                        this.enchantments::add,
                        objectHolder -> Util.makeDescriptionId("enchantment", objectHolder.getKey().location())
                );
            } else if (isGroupAddHovered(relativeX, relativeY)) {
                List<EnchantmentGroup> groupValues = new ArrayList<>(List.of(EnchantmentGroup.values()));
                groupValues.removeAll(this.groups);
                selector = new SelectEnumWidget<>(leftPos + 152, topPos + 53, 100, 80, font, groupValues, EnchantmentGroup::getName, this::addGroup, Component.translatable("cec.select_group"));
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 70, 10, 80, 60)) {
                int id = (int) ((relativeY - 11) / 10);
                if (id < this.enchantments.size()) this.enchantments.remove(id);
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 152, 10, 161, 60)) {
                int id = (int) ((relativeY - 11) / 10);
                if (id < this.groups.size()) this.groups.remove(id);
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 280, 11, 290, 21)) {
                this.bold = !this.bold;
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 280, 23, 290, 33)) {
                this.underlined = !this.underlined;
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 280, 35, 290, 45)) {
                this.italic = !this.italic;
            } else if (MathHelper.is2dBetween(relativeX, relativeY, 280, 47, 290, 57)) {
                selector = new SelectChatColorWidget(leftPos +  178, topPos + 56, this::setColor, Component.translatable("cec.select_color"), font, this.colorType);
            } else if (this.levelRange != null) {
                if (MathHelper.is2dBetween(relativeX, relativeY, 163, 22, 173, 32)) {
                    this.levelRange = new LevelRange(levelRange.getMin(), levelRange.getMax(), !levelRange.isMaxLevelRelative());
                } else if (MathHelper.is2dBetween(relativeX, relativeY, 165, 43, 185, 53)) {
                    if (levelRange.getMin() == levelRange.getMax() && !levelRange.isMaxLevelRelative()) return;
                    selector = new SelectCountWidget(leftPos + 102, topPos + 43, 200, font, levelRange.getMin(), this::setMinLevel, new IntegerNumberRange(-255, this.levelRange.getMax()), Component.translatable("cec.select_level_bound.min"));
                } else if (MathHelper.is2dBetween(relativeX, relativeY, 190, 43, 210, 53)) {
                    selector = new SelectCountWidget(leftPos + 102, topPos + 43, 200, font, levelRange.getMax(), this::setMaxLevel, new IntegerNumberRange(this.levelRange.getMin(), 255), Component.translatable("cec.select_level_bound.max"));
                }
            }
        }

        private void addGroup(EnchantmentGroup enchantmentGroup) {
            this.groups.add(enchantmentGroup);
            selector = null;
        }

        private void setColor(SelectChatColorWidget.ColorType colorType) {
            this.colorType = colorType;
        }

        private boolean isEnchantmentAddHovered(double relativeX, double relativeY) {
            if (selector != null) return false;
            int enchantmentAddY = 11 + enchantments.size() * 10;
            return MathHelper.is2dBetween(relativeX, relativeY, 4, enchantmentAddY, 12, enchantmentAddY + 8);
        }

        private boolean isGroupAddHovered(double relativeX, double relativeY) {
            if (selector != null) return false;
            int groupCount = groups.size();
            return groupCount < EnchantmentGroup.values().length &&
                    MathHelper.is2dBetween(relativeX, relativeY, 80, 11 + groupCount * 10, 88, 19 + groupCount * 10);
        }

        private EnchantmentColor color() {
            Style style = this.colorType.getStyle().withBold(bold).withUnderlined(underlined).withItalic(italic);
            return EnchantmentColor.create(name, this.enchantments, this.groups, levelRange, style);
        }

        public void toggleLevelReq() {
            if (this.levelRange != null) this.levelRange = null;
            else this.levelRange = new LevelRange(0, 0, false);
        }

        public void setMinLevel(int min) {
            if (this.levelRange != null) this.levelRange = this.levelRange.withMin(min);
        }


        public void setMaxLevel(int max) {
            if (this.levelRange != null) this.levelRange = this.levelRange.withMax(max);
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
            if (this.selector != null)
                this.selector = null;
            else this.onClose();
            return true;
        }
        return false;
    }
}