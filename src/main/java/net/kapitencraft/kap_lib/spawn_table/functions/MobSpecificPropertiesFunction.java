package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.io.serialization.ExtraJsonSerializers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

//TODO add pathfinding property changes
public class MobSpecificPropertiesFunction extends SpawnEntityConditionalFunction {
    private final LootContext.EntityTarget attackTarget;
    private final boolean canPickupLoot, persistenceRequired, noAi;
    private final ResourceLocation lootTable;

    protected MobSpecificPropertiesFunction(LootItemCondition[] pPredicates, LootContext.EntityTarget attackTarget, boolean canPickupLoot, boolean persistenceRequired, boolean noAi, ResourceLocation lootTable) {
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
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.MOB_PROPERTIES.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<MobSpecificPropertiesFunction> {

        @Override
        public void serialize(JsonObject pJson, MobSpecificPropertiesFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            if (pFunction.attackTarget != null) pJson.add("attackTarget", pSerializationContext.serialize(pFunction.attackTarget));
            if (pFunction.canPickupLoot) pJson.addProperty("canPickupLoot", true);
            if (pFunction.persistenceRequired) pJson.addProperty("persistenceRequired", true);
            if (pFunction.noAi) pJson.addProperty("noAi", true);
        }

        @Override
        public MobSpecificPropertiesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            LootContext.EntityTarget attackTarget = pObject.has("attackTarget") ? pDeserializationContext.deserialize(pObject.get("attackTarget"), LootContext.EntityTarget.class) : null;
            boolean canPickupLoot = GsonHelper.getAsBoolean(pObject, "canPickupLoot", false),
                    persistenceRequired = GsonHelper.getAsBoolean(pObject, "persistenceRequired", false),
                    noAi = GsonHelper.getAsBoolean(pObject, "noAi", false);
            ResourceLocation lootTable = pObject.has("loot_table") ? ExtraJsonSerializers.RL.deserialize(pObject.get("loot_table")) : null;
            return new MobSpecificPropertiesFunction(pConditions,
                    attackTarget,
                    canPickupLoot,
                    persistenceRequired,
                    noAi,
                    lootTable
            );
        }
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private LootContext.EntityTarget attackTarget;
        private boolean canPickupLoot, persistenceRequired, noAi;
        private ResourceLocation lootTable;

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

        public Builder setLootTable(ResourceLocation lootTable) {
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
            return new MobSpecificPropertiesFunction(
                    getConditions(),
                    attackTarget,
                    canPickupLoot,
                    persistenceRequired,
                    noAi,
                    lootTable
            );
        }
    }
}
