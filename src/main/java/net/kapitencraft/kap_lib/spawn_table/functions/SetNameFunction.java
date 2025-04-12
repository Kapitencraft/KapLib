package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNameFunction extends SpawnEntityConditionalFunction {
    private final Component name;

    public SetNameFunction(LootItemCondition[] conditions, Component name) {
        super(conditions);
        this.name = name;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_NAME.get();
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setCustomName(name);
        pEntity.setCustomNameVisible(true);
        return pEntity;
    }

    public static Builder setName(Component component) {
        return new Builder(component);
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final Component name;

        Builder(Component name) {
            this.name = name;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetNameFunction(this.getConditions(), name);
        }
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetNameFunction> {

        @Override
        public void serialize(JsonObject pJson, SetNameFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("name", Component.Serializer.toJsonTree(pFunction.name));
        }

        @Override
        public SetNameFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            Component component = Component.Serializer.fromJson(pObject.get("name"));
            return new SetNameFunction(pConditions, component);
        }
    }
}
