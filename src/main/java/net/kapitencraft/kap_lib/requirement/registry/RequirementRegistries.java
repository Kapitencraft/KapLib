package net.kapitencraft.kap_lib.requirement.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.requirement.conditions.abstracts.ReqCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface RequirementRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<RegistrySerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = syncReg(Keys.REQ_CONDITIONS);

    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).sync(true)
                .create();
        registries.add(registry);
        return registry;
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }

    interface Keys {
        ResourceKey<Registry<RegistrySerializer<? extends ReqCondition<?>>>> REQ_CONDITIONS = createRegistry("requirement_conditions");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
