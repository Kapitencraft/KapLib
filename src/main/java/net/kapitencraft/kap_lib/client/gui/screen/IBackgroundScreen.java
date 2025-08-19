package net.kapitencraft.kap_lib.client.gui.screen;

/**
 * interface for reducing effort when creating a screen with background
 */
public interface IBackgroundScreen {

    int getImageWidth();
    int getImageHeight();

    default int leftPos(int width) {
        return (width - this.getImageHeight()) / 2;
    }

    default int topPos(int height) {
        return (height - this.getImageHeight()) / 2;
    }

}
