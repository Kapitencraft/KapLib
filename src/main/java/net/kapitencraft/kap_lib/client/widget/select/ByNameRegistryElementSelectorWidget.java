package net.kapitencraft.kap_lib.client.widget.select;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ByNameRegistryElementSelectorWidget<T> extends SelectRegistryElementWidget<T> {
    private final Function<T, String> keyExtractor;

    public ByNameRegistryElementSelectorWidget(int x, int y, int width, int height, Component title, Font font, IForgeRegistry<T> registry, Function<T, String> keyExtractor, Consumer<T> valueSink) {
        super(x, y, width, height, title, font, registry, valueSink);
        this.keyExtractor = keyExtractor;
    }

    @Override
    protected int getHoveredIndex(double pMouseX, double pMouseY) {
        //yPos = this.y + this.scrollY + 12 * id - 1;
        return ((int) pMouseY - this.y - this.scrollY - 10) / 12;
    }

    @Override
    protected void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int middleX = this.width / 2 - 1;
        int id = 0;
        //y = this.scrollY + 12 * id + this.y;
        int hoveredIndex = (pMouseY - this.y - this.scrollY - 10) / 12;
        for (T t : allElements) {
            graphics.fill(1, this.scrollY + 12 * id - 1, width - 1, this.scrollY + 12 * id + 9, selected == t ? 0xFF404040 : id == hoveredIndex ? 0xFF303030 : 0xFF202020);
            graphics.drawCenteredString(this.font, Component.translatable(keyExtractor.apply(t)), middleX, this.scrollY + 12 * id, 0xFFFFFF);
            id++;
        }
    }

    @Override
    protected int size() {
        return allElements.size() * 12;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
}
