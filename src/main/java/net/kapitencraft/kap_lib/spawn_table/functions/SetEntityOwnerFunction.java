package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetEntityOwnerFunction extends SpawnEntityConditionalFunction {
    private final LootContext.EntityTarget target;

    protected SetEntityOwnerFunction(LootItemCondition[] pPredicates, LootContext.EntityTarget target) {
        super(pPredicates);
        this.target = target;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof TamableAnimal tamableAnimal) {
            tamableAnimal.setOwnerUUID(pContext.getParam(target.getParam()).getUUID());
            tamableAnimal.setTame(true);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_OWNER.get();
    }

    public static Builder builder(LootContext.EntityTarget target) {
        return new Builder(target);
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetEntityOwnerFunction> {

        @Override
        public void serialize(JsonObject pJson, SetEntityOwnerFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("target", pSerializationContext.serialize(pFunction.target));
        }

        @Override
        public SetEntityOwnerFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new SetEntityOwnerFunction(pConditions, pDeserializationContext.deserialize(pObject.get("target"), LootContext.EntityTarget.class));
        }
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final LootContext.EntityTarget target;

        public Builder(LootContext.EntityTarget target) {
            this.target = target;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetEntityOwnerFunction(getConditions(), target);
        }
    }

}
