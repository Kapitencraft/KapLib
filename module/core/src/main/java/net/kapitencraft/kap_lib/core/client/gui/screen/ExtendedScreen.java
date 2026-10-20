package net.kapitencraft.kap_lib.core.client.gui.screen;

import net.kapitencraft.kap_lib.core.client.gui.screen.tooltip.HoverTooltip;

/**
 * interface of a Screen that may contain a {@link HoverTooltip}
 */
public interface ExtendedScreen {
    void addHoverTooltip(HoverTooltip tooltip);
}
