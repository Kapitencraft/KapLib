package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetHealthFunction extends SpawnEntityConditionalFunction {
    private final int health;
    private final int absorption;

    protected SetHealthFunction(LootItemCondition[] pPredicates, int health, int absorption) {
        super(pPredicates);
        this.health = health;
        this.absorption = absorption;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof LivingEntity living) {
            living.setHealth(health);
            if (absorption != -1) living.setAbsorptionAmount(absorption);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_HEALTH.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetHealthFunction> {

        @Override
        public void serialize(JsonObject pJson, SetHealthFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.addProperty("health", pFunction.health);
            if (pFunction.absorption != -1) pJson.addProperty("absorption", pFunction.absorption);
        }

        @Override
        public SetHealthFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            int health = GsonHelper.getAsInt(pObject, "health");
            int absorption = GsonHelper.getAsInt(pObject, "absorption", -1);
            return new SetHealthFunction(pConditions, health, absorption);
        }
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final int health;
        private int absorption = -1;

        public Builder(int health) {
            this.health = health;
        }

        public Builder withAbsorption(int absorption) {
            this.absorption = absorption;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetHealthFunction(getConditions(), health, absorption);
        }
    }
}
