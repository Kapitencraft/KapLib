package net.kapitencraft.kap_lib.client.overlay.holder;

import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.function.Function;

/**
 * render a simple component
 */
public class SimpleOverlay extends Overlay {
    private final Function<LocalPlayer, Component> mapper;
    public SimpleOverlay(Component name, OverlayProperties holder, Function<LocalPlayer, Component> mapper) {
        super(holder, name);
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
    public void render(ForgeGui gui, GuiGraphics graphics, float posX, float posY, LocalPlayer player) {
        graphics.drawString(gui.getFont(), mapper.apply(player), 0, 0, -1);
    }
}
