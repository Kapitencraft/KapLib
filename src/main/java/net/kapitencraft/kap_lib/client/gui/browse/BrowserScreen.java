package net.kapitencraft.kap_lib.client.gui.browse;

import net.kapitencraft.kap_lib.client.gui.screen.DefaultBackgroundScreen;
import net.kapitencraft.kap_lib.client.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * used to render Browsables that create a history that you can browse throw (hence the name)
 * @param <T> the Type of {@link IBrowsable} this screen contains
 */
public abstract class BrowserScreen<T extends IBrowsable> extends DefaultBackgroundScreen {
    private final List<Widget> browseables = new ArrayList<>();
    protected final T browsable;

    public BrowserScreen(T browsable) {
        super(browsable.getName());
        this.browsable = browsable;
    }


    private byte lastDotCount = 0;

    protected void renderFetchingString(GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        MutableComponent toShow = Component.translatable("gui.fetching");
        graphics.drawCenteredString(font, toShow.append(" " + ".".repeat(Math.max(0, lastDotCount++))), (int) (this.leftPos + this.getImageWidth() / 2.), (int) (this.topPos + (this.getImageHeight() / 2.)), -1);
        if (lastDotCount == 3) lastDotCount = 0;
    }

    @Override
    public @NotNull List<Widget> children() {
        return this.browseables;
    }

}
