package net.kapitencraft.kap_lib.client;

import net.kapitencraft.kap_lib.client.cam.core.CameraController;
import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimationManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * client handler for cached information
 */
@OnlyIn(Dist.CLIENT)
public interface LibClient {


    /**
     * overlay controller; controls the given Overlays for the screen
     */
    OverlayManager overlays = OverlayManager.load();

    /**
     * handles particle animations
     */
    ParticleAnimationManager animations = new ParticleAnimationManager();

    /**
     * handles tracking shots
     */
    CameraController cameraControl = new CameraController();
}