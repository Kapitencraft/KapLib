package net.kapitencraft.kap_lib.registry.vanilla;

import net.kapitencraft.kap_lib.test.TestSwordItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaTestItems {
    DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "test");

    RegistryObject<Item> TEST_SWORD = REGISTRY.register("sword", TestSwordItem::new);
}
