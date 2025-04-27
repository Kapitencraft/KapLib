package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireFunction extends SpawnEntityConditionalFunction {
    private final Boolean visualFire;
    private final int duration;

    protected SetFireFunction(LootItemCondition[] pPredicates, Boolean visualFire, int duration) {
        super(pPredicates);
        this.visualFire = visualFire;
        this.duration = duration;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setRemainingFireTicks(duration);
        if (visualFire != null) pEntity.hasVisualFire = visualFire;
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_FIRE_DURATION.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetFireFunction> {

        @Override
        public void serialize(JsonObject pJson, SetFireFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.addProperty("duration", pFunction.duration);
            JsonHelper.addOptionalBool(pJson, "visual", pFunction.visualFire);
        }

        @Override
        public SetFireFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new SetFireFunction(pConditions, JsonHelper.getAsOptionalBool(pObject, "visual"), GsonHelper.getAsInt(pObject, "duration"));
        }
    }
}
