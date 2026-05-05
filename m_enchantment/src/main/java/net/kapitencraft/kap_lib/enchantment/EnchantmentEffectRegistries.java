package net.kapitencraft.kap_lib.enchantment;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBlockBreakEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentCountEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface EnchantmentEffectRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<MapCodec<? extends EnchantmentBowEffect>> BOW = reg(Keys.BOW);
    Registry<MapCodec<? extends EnchantmentCountEffect>> COUNT = reg(Keys.COUNT);
    Registry<MapCodec<? extends EnchantmentBlockBreakEffect>> BLOCK_BREAK = reg(Keys.BLOCK_BREAK);

    private static <T> Registry<T> reg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).create();
        registries.add(registry);
        return registry;
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }


    interface Keys {
        ResourceKey<Registry<MapCodec<? extends EnchantmentBowEffect>>> BOW = createRegistry("enchantment_bow_effects");
        ResourceKey<Registry<MapCodec<? extends EnchantmentCountEffect>>> COUNT = createRegistry("enchantment_count_effects");
        ResourceKey<Registry<MapCodec<? extends EnchantmentBlockBreakEffect>>> BLOCK_BREAK = createRegistry("enchantment_block_break_effects");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
