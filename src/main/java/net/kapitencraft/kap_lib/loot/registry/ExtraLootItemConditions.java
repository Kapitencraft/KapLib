package net.kapitencraft.kap_lib.loot.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.loot.conditions.LootTableTypeCondition;
import net.kapitencraft.kap_lib.loot.conditions.TagKeyCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraLootItemConditions {
    DeferredRegister<LootItemConditionType> REGISTRY = LibConstants.registry(Registries.LOOT_CONDITION_TYPE);

    Holder<LootItemConditionType> TAG_KEY = REGISTRY.register("tag_key", () -> new LootItemConditionType(TagKeyCondition.CODEC));
    Holder<LootItemConditionType> TYPE = REGISTRY.register("table_type", () -> new LootItemConditionType(LootTableTypeCondition.CODEC));
}