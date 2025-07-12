package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.item.test.TestItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface TestItems {

    DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "test");

    RegistryObject<TestItem> OBJECT = REGISTRY.register("test", TestItem::new);
}
