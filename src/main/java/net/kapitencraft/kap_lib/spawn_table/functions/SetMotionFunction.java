package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class SetMotionFunction extends SpawnEntityConditionalFunction {
    private final Vec3 motion;
    protected SetMotionFunction(LootItemCondition[] pPredicates, Vec3 motion) {
        super(pPredicates);
        this.motion = motion;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setDeltaMovement(motion);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_MOTION.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetMotionFunction> {

        @Override
        public SetMotionFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            Vec3 motion = JsonHelper.getAsVec3(pObject, "motion");
            return new SetMotionFunction(pConditions, motion);
        }
    }
}
