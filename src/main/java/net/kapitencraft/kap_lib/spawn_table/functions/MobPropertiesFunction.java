package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.core.Markers;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class MobPropertiesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<MobPropertiesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("attackTarget").forGetter(f -> Optional.ofNullable(f.attackTarget)),
            Codec.BOOL.optionalFieldOf("canPickupLoot", false).forGetter(f -> f.canPickupLoot),
            Codec.BOOL.optionalFieldOf("persistenceRequired", false).forGetter(f -> f.persistenceRequired),
            Codec.BOOL.optionalFieldOf("noAi", false).forGetter(f -> f.noAi),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("lootTable").forGetter(f -> Optional.ofNullable(f.lootTable))
    ).and(commonFields(i).t1()).apply(i, MobPropertiesFunction::new));

    private final LootContext.EntityTarget attackTarget;
    private final boolean canPickupLoot, persistenceRequired, noAi;
    private final ResourceKey<LootTable> lootTable;

    protected MobPropertiesFunction(Optional<LootContext.EntityTarget> attackTarget, boolean canPickupLoot, boolean persistenceRequired, boolean noAi, Optional<ResourceKey<LootTable>> lootTable, List<LootItemCondition> pPredicates) {
        this(attackTarget.orElse(null), canPickupLoot, persistenceRequired, noAi, lootTable.orElse(null), pPredicates);
    }

    protected MobPropertiesFunction(LootContext.EntityTarget attackTarget, boolean canPickupLoot, boolean persistenceRequired, boolean noAi, ResourceKey<LootTable> lootTable, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.attackTarget = attackTarget;
        this.canPickupLoot = canPickupLoot;
        this.persistenceRequired = persistenceRequired;
        this.noAi = noAi;
        this.lootTable = lootTable;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Mob mob) {
            if (attackTarget != null) {
                if (pContext.getParam(attackTarget.getParam()) instanceof LivingEntity living) {
                    mob.setTarget(living);
                } else KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "attack target {} was not living entity", pContext.getParam(attackTarget.getParam()));
            }
            if (canPickupLoot) mob.setCanPickUpLoot(true);
            if (persistenceRequired) mob.setPersistenceRequired();
            if (noAi) mob.setNoAi(true);
            if (lootTable != null) mob.lootTable = lootTable;
        } else KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "entity {} was no mob", pEntity);

        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.MOB_PROPERTIES.get();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private LootContext.EntityTarget attackTarget;
        private boolean canPickupLoot, persistenceRequired, noAi;
        private ResourceKey<LootTable> lootTable;

        public Builder setTarget(LootContext.EntityTarget target) {
            this.attackTarget = target;
            return this;
        }

        public Builder canPickupLoot() {
            this.canPickupLoot = true;
            return this;
        }

        public Builder persistenceRequired() {
            this.persistenceRequired = true;
            return this;
        }

        public Builder setLootTable(ResourceKey<LootTable> lootTable) {
            this.lootTable = lootTable;
            return this;
        }

        public Builder noAi() {
            this.noAi = true;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new MobPropertiesFunction(
                    attackTarget,
                    canPickupLoot,
                    persistenceRequired,
                    noAi,
                    lootTable,
                    getConditions()
            );
        }
    }
}
