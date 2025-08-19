package net.kapitencraft.kap_lib.client.gui.screen;

import net.kapitencraft.kap_lib.client.gui.screen.tooltip.HoverTooltip;

/**
 * interface of a Screen that may contain a {@link HoverTooltip}
 */
public interface IModScreen {

    void addHoverTooltip(HoverTooltip tooltip);
}
