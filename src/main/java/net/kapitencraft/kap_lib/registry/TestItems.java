package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.test.CooldownTestItem;
import net.kapitencraft.kap_lib.test.TestSwordItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface TestItems {

    DeferredRegister.Items REGISTRY = DeferredRegister.createItems("test");

    Supplier<CooldownTestItem> OBJECT = REGISTRY.register("test", CooldownTestItem::new);

    Holder<Item> TEST_SWORD = REGISTRY.register("sword", TestSwordItem::new);
}