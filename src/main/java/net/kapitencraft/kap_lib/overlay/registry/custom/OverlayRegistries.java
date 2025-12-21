package net.kapitencraft.kap_lib.overlay.registry.custom;

import net.kapitencraft.kap_lib.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.overlay.event.custom.client.RegisterConfigurableOverlaysEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface OverlayRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<OverlayProperties> OVERLAY_PROPERTIES = syncReg(Keys.OVERLAY_PROPERTIES);

    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).sync(true).create();
        registries.add(registry);
        return registry;
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }

    interface Keys {
        /**
         * default overlay properties. register inside {@link RegisterConfigurableOverlaysEvent#addOverlay(net.minecraft.core.Holder, Function) RegisterConfigurableOverlaysEvent#addOverlay}, to apply the overlay
         */
        ResourceKey<Registry<OverlayProperties>> OVERLAY_PROPERTIES = createRegistry("overlay_properties");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }

        private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
            return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(id));
        }

    }
}
