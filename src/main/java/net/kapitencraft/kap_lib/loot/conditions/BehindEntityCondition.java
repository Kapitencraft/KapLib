package net.kapitencraft.kap_lib.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class BehindEntityCondition implements LootItemCondition {
    public static final MapCodec<BehindEntityCondition> CODEC = Codec.unit(new BehindEntityCondition()).fieldOf("behind");

    @Override
    public @NotNull LootItemConditionType getType() {
        return ExtraLootItemConditions.BEHIND.value();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return lootContext.hasParam(LootContextParams.DIRECT_ATTACKING_ENTITY) && MathHelper.isBehind(lootContext.getParam(LootContextParams.DIRECT_ATTACKING_ENTITY), lootContext.getParam(LootContextParams.THIS_ENTITY));
    }
}
