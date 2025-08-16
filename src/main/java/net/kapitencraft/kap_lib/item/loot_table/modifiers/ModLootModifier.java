package net.kapitencraft.kap_lib.item.loot_table.modifiers;

import net.kapitencraft.kap_lib.item.loot_table.IConditional;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public abstract class ModLootModifier extends LootModifier implements IConditional {

    protected ModLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public LootItemCondition[] getConditions() {
        return conditions;
    }
}
