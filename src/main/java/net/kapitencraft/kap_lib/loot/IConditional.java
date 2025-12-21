package net.kapitencraft.kap_lib.loot;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface IConditional {

    LootItemCondition[] getConditions();
}
