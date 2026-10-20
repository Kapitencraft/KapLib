package net.kapitencraft.kap_lib.cooldown.loot;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownRegistries;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownLootItemConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * a loot condition to check whether a given cooldown is inactive
 *
 * @param cooldown the cooldown that is required to be inactive
 */
public record CooldownInactiveCondition(Cooldown cooldown) implements LootItemCondition {
    public static final MapCodec<CooldownInactiveCondition> CODEC = CooldownRegistries.COOLDOWNS.byNameCodec().xmap(CooldownInactiveCondition::new, CooldownInactiveCondition::cooldown).fieldOf("cooldown");

    @Override
    public LootItemConditionType getType() {
        return CooldownLootItemConditions.COOLDOWN_INACTIVE.value();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return lootContext.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity living && !this.cooldown.isActive(living);
    }
}
