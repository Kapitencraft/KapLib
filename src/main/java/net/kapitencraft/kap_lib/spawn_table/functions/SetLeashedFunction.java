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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLeashedFunction extends SpawnEntityConditionalFunction {
    private final BlockPos pos;
    private final LootContext.EntityTarget entity;

    protected SetLeashedFunction(LootItemCondition[] pPredicates, BlockPos pos, LootContext.EntityTarget entity) {
        super(pPredicates);
        this.pos = pos;
        this.entity = entity;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Mob mob) {
            Entity target = null;
            if (entity != null) {
                target = pContext.getParam(entity.getParam());
            } else if (pos != null) {
                target = new LeashFenceKnotEntity(pContext.getLevel(), pos);
            }
            if (target == null) KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "unable to create leash position");
            else mob.setLeashedTo(target, false);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_LEASHED.get();
    }

    public static Builder at(BlockPos pos) {
        return new Builder(pos, null);
    }

    public static Builder with(LootContext.EntityTarget target) {
        return new Builder(null, target);
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetLeashedFunction> {

        @Override
        public void serialize(JsonObject pJson, SetLeashedFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            if (pFunction.pos != null) pJson.add("pos", ExtraJsonSerializers.BLOCKPOS.serialize(pFunction.pos));
            else if (pFunction.entity != null) pJson.add("entity", pSerializationContext.serialize(pFunction.entity));
        }

        @Override
        public SetLeashedFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            LootContext.EntityTarget target = pObject.has("entity") ? pDeserializationContext.deserialize(pObject.get("entity"), LootContext.EntityTarget.class) : null;
            BlockPos blockPos = pObject.has("pos") ? ExtraJsonSerializers.BLOCKPOS.deserialize(pObject.get("pos")) : null;
            return new SetLeashedFunction(pConditions, blockPos, target);
        }
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final BlockPos pos;
        private final LootContext.EntityTarget entityTarget;

        public Builder(BlockPos pos, LootContext.EntityTarget entityTarget) {
            this.pos = pos;
            this.entityTarget = entityTarget;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetLeashedFunction(getConditions(), pos, entityTarget);
        }
    }
}
