package net.kapitencraft.kap_lib.camera.registry.custom;

import net.kapitencraft.kap_lib.camera.modifiers.Modifier;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface CameraRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<Modifier.Type<?>> CAMERA_MODIFIERS = syncReg(Keys.CAMERA_MODIFIERS);

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

        //CAMERA CONTROL
        ResourceKey<Registry<Modifier.Type<?>>> CAMERA_MODIFIERS = createRegistry("camera_modifiers");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
