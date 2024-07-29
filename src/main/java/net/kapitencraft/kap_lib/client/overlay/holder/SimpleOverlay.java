package net.kapitencraft.kap_lib.client.overlay.holder;

import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/**
 * render a simple component
 */
public class SimpleOverlay extends Overlay {
    private final Function<LocalPlayer, Component> mapper;
    public SimpleOverlay(OverlayProperties holder, Function<LocalPlayer, Component> mapper) {
        super(holder);
        this.mapper = mapper;
    }

    @Override
    public float getWidth(LocalPlayer player, Font font) {
        return font.width(mapper.apply(player));
    }

    @Override
    public float getHeight(LocalPlayer player, Font font) {
        return 9;
    }

    @Override
    public void render(GuiGraphics graphics, float posX, float posY, LocalPlayer player) {
        renderString(graphics, mapper.apply(player), 0);
    }
}