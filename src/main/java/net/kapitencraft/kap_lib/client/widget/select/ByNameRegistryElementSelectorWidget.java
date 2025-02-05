package net.kapitencraft.kap_lib.client.widget.select;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Function;

public class ByNameRegistryElementSelectorWidget<T> extends SelectRegistryElementWidget<T> {
    private final IForgeRegistry<T> registry;
    private final Function<T, String> keyExtractor;

    protected ByNameRegistryElementSelectorWidget(int x, int y, int width, int height, Component title, Font font, IForgeRegistry<T> registry, Function<T, String> keyExtractor) {
        super(x, y, width, height, title, font);
        this.registry = registry;
        this.keyExtractor = keyExtractor;
    }

    @Override
    protected void renderInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int middleX = this.width / 2 - 1;
        int id = 0;
        for (T t : registry) {
            graphics.drawCenteredString(this.font, Component.translatable(keyExtractor.apply(t)), middleX, this.scrollY + this.y * id, 0xFFFFFF);
            id++;
        }
    }
}
