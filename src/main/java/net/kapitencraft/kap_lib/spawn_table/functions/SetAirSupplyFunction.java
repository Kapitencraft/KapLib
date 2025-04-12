package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetAirSupplyFunction extends SpawnEntityConditionalFunction {
    private final int supply;

    protected SetAirSupplyFunction(LootItemCondition[] pPredicates, int supply) {
        super(pPredicates);
        this.supply = supply;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setAirSupply(supply);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_AIR_SUPPLY.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetAirSupplyFunction> {

        @Override
        public void serialize(JsonObject pJson, SetAirSupplyFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.addProperty("supply", pFunction.supply);
        }

        @Override
        public SetAirSupplyFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new SetAirSupplyFunction(pConditions, GsonHelper.getAsInt(pObject, "supply"));
        }
    }
}
