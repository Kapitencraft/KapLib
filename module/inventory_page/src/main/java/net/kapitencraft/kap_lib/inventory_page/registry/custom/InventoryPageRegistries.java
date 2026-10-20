package net.kapitencraft.kap_lib.inventory_page.registry.custom;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.inventory_page.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface InventoryPageRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<WearableSlot> WEARABLE_SLOTS = reg(Keys.WEARABLE_SLOTS);
    Registry<InventoryPageType<?>> INVENTORY_PAGES = reg(Keys.INVENTORY_PAGES);

    private static <T> Registry<T> reg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).create();
        registries.add(registry);
        return registry;
    }

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

        ResourceKey<Registry<WearableSlot>> WEARABLE_SLOTS = createRegistry("wearable_slots");

        ResourceKey<Registry<InventoryPageType<?>>> INVENTORY_PAGES = createRegistry("inventory_pages");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }

        private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
            return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(id));
        }

    }
}
