package net.kapitencraft.kap_lib.overlay.event.custom.client;

import net.kapitencraft.kap_lib.core.stream.Consumers;
import net.kapitencraft.kap_lib.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.overlay.holder.Overlay;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * event class to register custom configurable overlays to the OverlayManager for rendering and configuration on the screen
 * fired on the Mod-Specific bus, only on the {@link Dist#CLIENT}
 */
@OnlyIn(Dist.CLIENT)
public class RegisterConfigurableOverlaysEvent extends Event implements IModBusEvent {
    private final Consumers.C3<ResourceLocation, OverlayProperties, Function<OverlayProperties, Overlay>> constructorFactory;

    public RegisterConfigurableOverlaysEvent(Consumers.C3<ResourceLocation, OverlayProperties, Function<OverlayProperties, Overlay>> constructorFactory) {
        this.constructorFactory = constructorFactory;
    }

    /**
     * @param location the screen location the renderer should default to
     * @param constructor the constructor being called to create the Holder
     */
    public void addOverlay(ResourceLocation location, OverlayProperties properties, Function<OverlayProperties, Overlay> constructor) {
        constructorFactory.apply(location, properties, constructor);
    }
}
