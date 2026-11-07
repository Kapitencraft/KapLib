package net.kapitencraft.kap_lib.bonus.registry;

import net.kapitencraft.kap_lib.bonus.BonusEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public interface BonusRegistries {

    Registry<RegistrySerializer<? extends BonusEffect>> EFFECT_SERIALIZERS = syncReg(Keys.EFFECT_SERIALIZERS);

    @SuppressWarnings("SameParameterValue")
    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).sync(true).create();
    }

    interface Keys {
        ResourceKey<Registry<RegistrySerializer<? extends BonusEffect>>> EFFECT_SERIALIZERS = createRegistry("bonus_serializers");

        @SuppressWarnings("SameParameterValue")
        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
