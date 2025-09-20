package net.kapitencraft.kap_lib.item.loot_table.conditions;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.registry.ExtraLootItemConditions;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record CooldownInactiveCondition(Cooldown cooldown) implements LootItemCondition {
    public static final MapCodec<CooldownInactiveCondition> CODEC = ExtraRegistries.COOLDOWNS.byNameCodec().xmap(CooldownInactiveCondition::new, CooldownInactiveCondition::cooldown).fieldOf("cooldown");

    @Override
    public LootItemConditionType getType() {
        return ExtraLootItemConditions.COOLDOWN_INACTIVE.value();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return lootContext.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity living && !this.cooldown.isActive(living);
    }
}
