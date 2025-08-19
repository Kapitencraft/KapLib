package net.kapitencraft.kap_lib.client.gui.browse;

import net.kapitencraft.kap_lib.client.gui.screen.DefaultBackgroundScreen;
import net.minecraft.network.chat.Component;

/**
 * screen to indicate that something is still loading
 * <br> and has to be waited for until it can change to {@code onComplete}
 */
public class LoadingScreen extends DefaultBackgroundScreen {
    private final DefaultBackgroundScreen onComplete;

    protected LoadingScreen(Component pTitle, DefaultBackgroundScreen onComplete) {
        super(pTitle);
        this.onComplete = onComplete;
    }
}
