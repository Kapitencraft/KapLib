package net.kapitencraft.kap_lib.client.overlay.holder;

import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;
import java.util.function.Function;

/**
 * render multiple chained components below each other
 */
public class MultiLineOverlay extends Overlay {
    private final float yChange;
    private final List<Function<LocalPlayer, Component>> list;
    public MultiLineOverlay(Component name, OverlayProperties holder, float yChange, List<Function<LocalPlayer, Component>> allText) {
        super(holder, name);
        this.yChange = yChange;
        this.list = allText;
    }

    @Override
    public float getWidth(LocalPlayer player, Font font) {
        return TextHelper.getWidthFromMultiple(list.stream().map(func -> func.apply(player)).toList(), font);
    }

    @Override
    public float getHeight(LocalPlayer player, Font font) {
        return list.size() * -yChange;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float width, float height, LocalPlayer player) {
        for (int i = 0; i < list.size(); i++) {
            Function<LocalPlayer, Component> mapper = list.get(i);
            graphics.drawString(gui.getFont(), mapper.apply(player), 0, -(int) yChange * i, -1);
        }
    }
}
