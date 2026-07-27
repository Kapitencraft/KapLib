package net.kapitencraft.kap_lib.cooldown.registry;

import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface CooldownRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<Cooldown> COOLDOWNS = syncReg(Keys.COOLDOWNS);

    @SuppressWarnings("SameParameterValue")
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

        ResourceKey<Registry<Cooldown>> COOLDOWNS = createRegistry("cooldowns");

        @SuppressWarnings("SameParameterValue")
        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }

        private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
            return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(id));
        }

    }
}
