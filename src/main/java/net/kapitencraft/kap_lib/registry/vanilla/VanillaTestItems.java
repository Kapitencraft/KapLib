package net.kapitencraft.kap_lib.registry.vanilla;

import net.kapitencraft.kap_lib.test.TestSwordItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface VanillaTestItems {
    DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, "test");

    Holder<Item> TEST_SWORD = REGISTRY.register("sword", TestSwordItem::new);
}
