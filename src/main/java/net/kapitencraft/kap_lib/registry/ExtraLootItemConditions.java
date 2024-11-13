package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.loot_table.conditions.LootTableTypeCondition;
import net.kapitencraft.kap_lib.item.loot_table.conditions.TagKeyCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraLootItemConditions {
    DeferredRegister<LootItemConditionType> REGISTRY = KapLibMod.registry(Registries.LOOT_CONDITION_TYPE);

    RegistryObject<LootItemConditionType> TAG_KEY = REGISTRY.register("tag_key", ()-> new LootItemConditionType(TagKeyCondition.SERIALIZER));
    RegistryObject<LootItemConditionType> TYPE = REGISTRY.register("table_type", ()-> new LootItemConditionType(LootTableTypeCondition.SERIALIZER));
}