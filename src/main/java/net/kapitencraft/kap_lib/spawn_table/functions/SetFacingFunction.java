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
import net.minecraft.world.phys.Vec2;

public class SetFacingFunction extends SpawnEntityConditionalFunction {
    private final Vec2 rot;

    protected SetFacingFunction(LootItemCondition[] pPredicates, Vec2 rot) {
        super(pPredicates);
        this.rot = rot;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setXRot(rot.x);
        pEntity.setYRot(rot.y);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_FACING.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetFacingFunction> {

        @Override
        public void serialize(JsonObject pJson, SetFacingFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.addProperty("pitch", pFunction.rot.x);
            pJson.addProperty("yaw", pFunction.rot.y);
        }

        @Override
        public SetFacingFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            float pitch = GsonHelper.getAsFloat(pObject, "pitch");
            float yaw = GsonHelper.getAsFloat(pObject, "yaw");
            return new SetFacingFunction(pConditions, new Vec2(pitch, yaw));
        }
    }
}
