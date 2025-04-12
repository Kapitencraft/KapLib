package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnEntity;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetExperienceValueFunction extends SpawnEntityConditionalFunction {
    private final int value;

    protected SetExperienceValueFunction(LootItemCondition[] pPredicates, int value) {
        super(pPredicates);
        this.value = value;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof ExperienceOrb orb) {
            orb.value = value;
        } else logWrongType("ExperienceOrb", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_EXPERIENCE_VALUE.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetExperienceValueFunction> {

        @Override
        public void serialize(JsonObject pJson, SetExperienceValueFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.addProperty("value", pFunction.value);
        }

        @Override
        public SetExperienceValueFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new SetExperienceValueFunction(pConditions, GsonHelper.getAsInt(pObject, "value"));
        }
    }

    public static Builder builder(int value) {
        return new Builder(value);
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final int value;

        public Builder(int value) {
            this.value = value;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetExperienceValueFunction(getConditions(), value);
        }
    }
}
