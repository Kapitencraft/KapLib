package net.kapitencraft.kap_lib.client.widget.select;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class SelectItemWidget extends SelectRegistryElementWidget<Item> {
    private static final int ITEM_WIDTH_WITH_OFFSET = 18;
    private static final List<ItemStack> itemsCache = StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(), false).map(Item::getDefaultInstance).toList();
    private final int xOffset = (this.width - 2) % 18 / 2;
    private final int elementsPerRow = (this.width - 2 - xOffset * 2) / 18;
    private final int maxHeight = ITEM_WIDTH_WITH_OFFSET * (itemsCache.size() / elementsPerRow);

    protected SelectItemWidget(int x, int y, int width, int height, Component title, Font font, Consumer<Item> itemSink) {
        super(x, y, width, height, title, font, ForgeRegistries.ITEMS, itemSink);
    }

    @Override
    protected int getHoveredIndex(double pMouseX, double pMouseY) {
        return 0;
    }

    @Override
    protected void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int minIndex = scrollY / ITEM_WIDTH_WITH_OFFSET * elementsPerRow;
        int maxIndex = (scrollY + this.height) / ITEM_WIDTH_WITH_OFFSET * elementsPerRow;
        for (int i = minIndex; i < maxIndex; i++) {
            int column = i % elementsPerRow;
            int row = i / elementsPerRow;
            graphics.renderItem(itemsCache.get(i), this.xOffset + column * ITEM_WIDTH_WITH_OFFSET, scrollY + row * ITEM_WIDTH_WITH_OFFSET);
        }
    }

    @Override
    protected int size() {
        return allElements.size() / elementsPerRow * 18;
    }
}
