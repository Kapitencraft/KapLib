package net.kapitencraft.kap_lib.overlay;

public interface OverlayPositions {
    OverlayProperties STATS = new OverlayProperties(-188.75f, 24f, .75f, .75f, OverlayProperties.Alignment.MIDDLE, OverlayProperties.Alignment.BOTTOM_RIGHT);
    OverlayProperties MANA = new OverlayProperties(2f, 2, 1, 1, OverlayProperties.Alignment.TOP_LEFT, OverlayProperties.Alignment.TOP_LEFT).setVisible(false);
}
