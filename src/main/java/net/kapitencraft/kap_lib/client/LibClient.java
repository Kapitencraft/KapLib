package net.kapitencraft.kap_lib.client;

import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColorManager;
import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimationManager;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.request.RequestHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
     * handles the server data requests
     */
    RequestHandler requests = new RequestHandler(ModMessages::sendToServer);

    /**
     * handles particle animations
     */
    ParticleAnimationManager animations = new ParticleAnimationManager();

    EnchantmentColorManager enchantmentColors = EnchantmentColorManager.load();
}