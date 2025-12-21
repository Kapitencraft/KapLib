package net.kapitencraft.kap_lib.spawn_table.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryType;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface SpawnTableRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<SpawnEntityFunctionType<?>> SPAWN_FUNCTION_TYPES = reg(Keys.FUNCTION_TYPES);
    Registry<SpawnPoolEntryType> SPAWN_POOL_ENTRY_TYPES = reg(Keys.POOL_ENTRY_TYPES);


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
        ResourceKey<Registry<SpawnEntityFunctionType<?>>> FUNCTION_TYPES = createRegistry("spawn_table/function_types");
        ResourceKey<Registry<SpawnPoolEntryType>> POOL_ENTRY_TYPES = createRegistry("spawn_table/pool_entry_types");
        ResourceKey<Registry<SpawnTable>> SPAWN_TABLES = createRegistry("spawn_tables");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
