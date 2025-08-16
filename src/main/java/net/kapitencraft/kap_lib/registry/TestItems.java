package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.item.test.TestItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface TestItems {

    DeferredRegister<Item> REGISTRY = DeferredRegister.createItems("test");

    Supplier<TestItem> OBJECT = REGISTRY.register("test", TestItem::new);
}
