package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.enchantments.extras.TestEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface TestEnchantments {

    DeferredRegister<Enchantment> REGISTRY = KapLibMod.registry(ForgeRegistries.ENCHANTMENTS);

    RegistryObject<TestEnchantment> TEST = REGISTRY.register("test", TestEnchantment::new);
}
