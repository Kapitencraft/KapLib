package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.enchantments.abstracts.ModBowEnchantment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface ExtraRegistryCallbacks {

    class EnchantmentCallback implements IForgeRegistry.AddCallback<Enchantment> {
        @Override
        public void onAdd(IForgeRegistryInternal<Enchantment> owner, RegistryManager stage, int id, ResourceKey<Enchantment> key, Enchantment obj, @Nullable Enchantment oldObj) {
            if (oldObj instanceof ModBowEnchantment) {
                ModBowEnchantment.executionMap.remove(key.location());
            }
            if (obj instanceof ModBowEnchantment bowEnchantment) {
                ModBowEnchantment.executionMap.put(key.location(), bowEnchantment::execute);
            }
        }
    }
}
