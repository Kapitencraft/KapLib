package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.enchantments.abstracts.ModBowEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.callback.AddCallback;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ExtraRegistryCallbacks {

    class EnchantmentCallback implements AddCallback<Enchantment> {

        @Override
        public void onAdd(Registry<Enchantment> registry, int id, ResourceKey<Enchantment> key, Enchantment value) {
            if (value instanceof ModBowEnchantment bowEnchantment) {
                ModBowEnchantment.executionMap.put(key.location(), bowEnchantment::execute);
            }
        }
    }
}
