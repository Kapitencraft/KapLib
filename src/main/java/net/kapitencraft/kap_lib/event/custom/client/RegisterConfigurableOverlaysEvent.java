package net.kapitencraft.kap_lib.event.custom.client;

import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.overlay.holder.Overlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * event class to register custom configurable overlays to the OverlayManager for rendering and configuration on the screen
 * fired on the Mod-Specific bus, only on the {@link Dist#CLIENT}
 */
@OnlyIn(Dist.CLIENT)
public class RegisterConfigurableOverlaysEvent extends Event implements IModBusEvent {
    private final BiConsumer<RegistryObject<OverlayProperties>, Function<OverlayProperties, Overlay>> constructorFactory;

    public RegisterConfigurableOverlaysEvent(BiConsumer<RegistryObject<OverlayProperties>, Function<OverlayProperties, Overlay>> constructorFactory) {
        this.constructorFactory = constructorFactory;
    }

    /**
     * @param location the screen location the renderer should default to
     * @param constructor the constructor being called to create the Holder
     */
    public void addOverlay(RegistryObject<OverlayProperties> location, Function<OverlayProperties, Overlay> constructor) {
        constructorFactory.accept(location, constructor);
    }
}
