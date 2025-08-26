package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RaiderPropertiesFunction extends SpawnEntityConditionalFunction {
    private final Boolean canJoinRaid, celebrating;

    protected RaiderPropertiesFunction(LootItemCondition[] pPredicates, Boolean canJoinRaid, Boolean celebrating) {
        super(pPredicates);
        this.canJoinRaid = canJoinRaid;
        this.celebrating = celebrating;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Raider raider) {
            if (canJoinRaid != null) raider.setCanJoinRaid(canJoinRaid);
            if (celebrating != null) raider.setCelebrating(celebrating);
        } else logWrongType("Raider", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.RAIDER_PROPERTIES.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<RaiderPropertiesFunction> {

        @Override
        public void serialize(JsonObject pJson, RaiderPropertiesFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            JsonHelper.addOptionalBool(pJson, "canJoinRaid", pFunction.canJoinRaid);
            JsonHelper.addOptionalBool(pJson, "celebrating", pFunction.celebrating);
        }

        @Override
        public RaiderPropertiesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            Boolean canJoinRaid = JsonHelper.getAsOptionalBool(pObject, "canJoinRaid");
            Boolean celebrating = JsonHelper.getAsOptionalBool(pObject, "celebrating");

            return new RaiderPropertiesFunction(pConditions, canJoinRaid, celebrating);
        }
    }
}
