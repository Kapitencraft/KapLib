package net.kapitencraft.kap_lib.core.client.widget.select;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class SelectBlockWidget extends SelectRegistryElementWidget<Block> {
    private static final int ITEM_WIDTH_WITH_OFFSET = 18;
    private static final List<ItemStack> ITEMS_CACHE = StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false)
            .map(Block::asItem)
            .filter(item -> item != Items.AIR)
            .map(Item::getDefaultInstance)
            .toList();
    private static final List<Block> BLOCKS = ITEMS_CACHE.stream().map(ItemStack::getItem).map(Block::byItem).toList();
    private final int xOffset = (this.width - 2) % 18 / 2;

    protected SelectBlockWidget(int x, int y, int width, int height, Component title, Font font, Consumer<Block> itemSink) {
        super(x, y, width, height, title, font, BLOCKS, itemSink);
    }

    @Override
    protected int getHoveredIndex(double pMouseX, double pMouseY) {
        pMouseX -= this.x + 1;
        pMouseY -= this.y + 11;
        //scroll + y * ITEM_WIDTH_WITH_OFFSET = pMouseY
        //this.xOffset + x * ITEM_WIDTH_WITH_OFFSET = pMouseX

        return ((int) (pMouseY - scroll) / ITEM_WIDTH_WITH_OFFSET * getEPR()) + ((int) (pMouseX - this.xOffset) / ITEM_WIDTH_WITH_OFFSET);
    }

    @Override
    protected void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int elementsPerRow = getEPR();
        int minIndex = (int) scroll / ITEM_WIDTH_WITH_OFFSET * elementsPerRow;
        int maxIndex = ((int) scroll + this.height) / ITEM_WIDTH_WITH_OFFSET * elementsPerRow;
        for (int i = minIndex; i < maxIndex; i++) {
            int column = i % elementsPerRow;
            int row = i / elementsPerRow;
            graphics.renderItem(ITEMS_CACHE.get(i), this.xOffset + column * ITEM_WIDTH_WITH_OFFSET,  (int) scroll + row * ITEM_WIDTH_WITH_OFFSET);
        }
    }

    private int getEPR() {
        return (this.width - 2 - xOffset * 2) / 18;
    }

    @Override
    protected int size() {
        return allElements.size() / getEPR() * 18;
    }
}
