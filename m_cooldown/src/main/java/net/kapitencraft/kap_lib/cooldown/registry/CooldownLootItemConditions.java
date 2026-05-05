package net.kapitencraft.kap_lib.cooldown.registry;

import net.kapitencraft.kap_lib.cooldown.loot.CooldownInactiveCondition;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface CooldownLootItemConditions {
    DeferredRegister<LootItemConditionType> REGISTRY = LibConstants.registry(Registries.LOOT_CONDITION_TYPE);

    Holder<LootItemConditionType> COOLDOWN_INACTIVE = REGISTRY.register("cooldown_inactive", () -> new LootItemConditionType(CooldownInactiveCondition.CODEC));
}