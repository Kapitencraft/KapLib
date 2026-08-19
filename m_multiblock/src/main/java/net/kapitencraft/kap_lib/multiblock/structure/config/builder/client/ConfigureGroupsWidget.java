package net.kapitencraft.kap_lib.multiblock.structure.config.builder.client;

import net.kapitencraft.kap_lib.core.client.widget.ScrollableWidget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfigurationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigureGroupsWidget extends ScrollableWidget {
    private static final Component ADD_TEXT = Component.translatable("cmsb.add");
    private static final Component REMOVE_TEXT = Component.translatable("cmsb.remove");
    private static final ResourceLocation BACKGROUND_LOC = ResourceLocation.withDefaultNamespace("widget/text_field");
    protected static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

    private final List<Entry> entries = new ArrayList<>();
    private int selectedIndex = -1;

    public ConfigureGroupsWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public void importFrom(MultiblockStructureConfigurationBlockEntity entity) {
        Map<String, MultiblockStructureConfiguration.BlockGroup> groups = entity.getGroups();
        groups.forEach((s, blockGroup) -> {
            this.entries.add(new Entry(s));
        });
    }

    @Override
    protected void updateScroll(boolean ignoreCursor) {
        this.scrollY = Math.clamp(this.scrollY, -(entries.size() * 20 - this.height), 0);
    }

    @Override
    protected int valueSize(boolean x) {
        return x ? this.width : entries.size() * 20;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(
                BACKGROUND_LOC,
                this.getX(),
                this.getY(),
                this.width,
                this.height - 22
        );
        int buttonYEnd = this.getY() + this.height;
        Minecraft minecraft = Minecraft.getInstance();
        //add button
        guiGraphics.blitSprite(
                BUTTON_SPRITES.get(
                        true,
                        MathHelper.is2dBetween(
                                mouseX, mouseY,
                                this.getX(), buttonYEnd - 20,
                                this.getX() + this.getWidth() / 2 - 5,
                                buttonYEnd
                        )
                ),
                this.getX(),
                buttonYEnd - 20,
                width / 2 - 5,
                20
        );
        renderScrollingString(
                guiGraphics,
                minecraft.font,
                ADD_TEXT,
                this.getX(),
                buttonYEnd - 20,
                this.getX() + width / 2 - 5,
                buttonYEnd,
                -1
        );
        //remove button
        guiGraphics.blitSprite(
                BUTTON_SPRITES.get(
                        true,
                        MathHelper.is2dBetween(mouseX, mouseY,
                                this.getX() + width / 2 + 5,
                                buttonYEnd - 20,
                                this.getX() + width,
                                buttonYEnd
                        )
                ),
                this.getX() + width / 2 + 5,
                buttonYEnd - 20,
                width / 2 - 5,
                20
        );
        renderScrollingString(guiGraphics,
                minecraft.font,
                REMOVE_TEXT,
                this.getX() + width / 2 + 5,
                buttonYEnd - 20,
                this.getX() + width,
                buttonYEnd,
                -1
        );
        //offset = 1

        guiGraphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1, this.getY() + this.getHeight() - 23);

        int y = this.getY() + (int) scrollY + 1;
        for (Entry entry : this.entries) {
            entry.render(guiGraphics, this.getX() + 1, y, mouseX, mouseY);
            y += entry.height();
        }

        guiGraphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int buttonYEnd = this.getY() + this.height;

        if (MathHelper.is2dBetween(mouseX, mouseY, this.getX(), buttonYEnd - 20, this.getX() + width / 2 - 5, buttonYEnd)) {
            //add
            this.entries.add(new Entry("entry" + this.entries.size()));
            return true;
        } else if (MathHelper.is2dBetween(mouseX, mouseY, this.getX() + width / 2 + 5, buttonYEnd - 20, this.getX() + width, buttonYEnd)) {
            //remove
            if (selectedIndex > -1) {
                this.entries.remove(selectedIndex);
            }
            return true;
        }

        int y = this.getY() + 1;
        for (int i = 0; i < this.entries.size(); i++) {
            Entry entry = this.entries.get(i);
            if (mouseY > y && mouseY < y + entry.height()) {
                this.selectedIndex = i;
                entry.mouseClicked(mouseX - getX(), mouseY - y, button);
            }
            y += entry.height();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private class Entry {
        private String name;
        private boolean extended = false;

        public Entry(String name) {
            this.name = name;
        }

        public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
            //graphics.fill(x, y, x + 150, y + 20, -1);
            graphics.drawString(Minecraft.getInstance().font, this.name, x + 2, y + 2, -1);
        }

        public void mouseClicked(double mouseX, double mouseY, int button) {
            int width = ConfigureGroupsWidget.this.width;
            if (mouseY > 0 && mouseY < 10) {
                if (mouseX > 0 && mouseX < width - 10) {
                    this.extended = !this.extended;
                } else {
                    //change name
                }
            }
        }

        public int height() {
            return !extended ? 20 : 20;
        }
    }
}
