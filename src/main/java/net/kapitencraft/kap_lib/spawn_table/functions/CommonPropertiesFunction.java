package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CommonPropertiesFunction extends SpawnEntityConditionalFunction {
    private final boolean noGravity, silent, invulnerable, glowing;

    protected CommonPropertiesFunction(LootItemCondition[] pPredicates, String... properties) {
        super(pPredicates);
        boolean noGravity = false,
                silent = false,
                invulnerable = false,
                glowing = false;
        for (String s : properties) {
            switch (s) {
                case "noGravity" -> noGravity = true;
                case "silent" -> silent = true;
                case "invulnerable" -> invulnerable = true;
                case "glowing" -> glowing = true;
            }
        }
        this.noGravity = noGravity;
        this.silent = silent;
        this.invulnerable = invulnerable;
        this.glowing = glowing;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (noGravity) pEntity.setNoGravity(true);
        if (silent) pEntity.setSilent(true);
        if (invulnerable) pEntity.setInvulnerable(true);
        if (glowing) pEntity.setGlowingTag(true);
        return null;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.COMMON_PROPERTIES.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<CommonPropertiesFunction> {

        @Override
        public CommonPropertiesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            String[] data = pDeserializationContext.deserialize(pObject.get("properties"), String[].class);
            return new CommonPropertiesFunction(pConditions, data);
        }
    }
}
