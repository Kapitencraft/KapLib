package net.kapitencraft.kap_lib.item.loot_table;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public interface IConditional {

    List<LootItemCondition> getConditions();
}
