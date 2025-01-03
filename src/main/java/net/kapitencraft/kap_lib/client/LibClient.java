package net.kapitencraft.kap_lib.client;

import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.particle.animation.ParticleAnimationManager;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.request.RequestHandler;

/**
 * client handler for cached information
 */
public interface LibClient {


    /**
     * overlay controller; controls the given Overlays for the screen
     */
    OverlayManager controller = OverlayManager.load();


    /**
     * handles the server data requests
     */
    RequestHandler handler = new RequestHandler(ModMessages::sendToServer);

    /**
     * handles particle animations
     */
    ParticleAnimationManager particleManager = new ParticleAnimationManager();
}
