package net.kapitencraft.kap_lib.overlay.event.handler;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.overlay.OverlayManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class OverlayModClientEvents {

    /**
     * register the renderer
     */
    @SubscribeEvent
    public static void overlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LibConstants.res("overlay"), OverlayManager.INSTANCE::render);
    }
}
