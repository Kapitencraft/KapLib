package net.kapitencraft.kap_lib.bonus.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface BonusRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<RegistrySerializer<? extends Bonus<?>>> SERIALIZERS = syncReg(Keys.SERIALIZERS);

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
        ResourceKey<Registry<RegistrySerializer<? extends Bonus<?>>>> SERIALIZERS = createRegistry("bonus_serializers");

        @SuppressWarnings("SameParameterValue")
        private static <T > ResourceKey < Registry < T >> createRegistry(String id) {
        return ResourceKey.createRegistryKey(LibConstants.res(id));
    }
    }
}
