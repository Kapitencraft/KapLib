package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnPool;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AddPassengersFunction extends SpawnEntityConditionalFunction {
    private final SpawnPool pool;

    protected AddPassengersFunction(LootItemCondition[] pPredicates, SpawnPool pool) {
        super(pPredicates);
        this.pool = pool;
    }

    public static Builder builder(SpawnPool.Builder builder) {
        return new Builder(builder);
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.getIndirectPassengers();
        pool.addRandomEntities(entity -> {
            entity.startRiding(pEntity, true);
        }, pContext);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.ADD_PASSENGERS.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<AddPassengersFunction> {

        @Override
        public void serialize(JsonObject pJson, AddPassengersFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("pool", pSerializationContext.serialize(pFunction.pool));
        }

        @Override
        public AddPassengersFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new AddPassengersFunction(pConditions, pDeserializationContext.deserialize(pObject.get("pool"), SpawnPool.class));
        }
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final SpawnPool builder;

        public Builder(SpawnPool.Builder builder) {
            this.builder = builder.build();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new AddPassengersFunction(getConditions(), builder);
        }
    }


}
