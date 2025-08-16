package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetEntityOwnerFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetEntityOwnerFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(f -> f.target)
    ).and(commonFields(i).t1()).apply(i, SetEntityOwnerFunction::new));

    private final LootContext.EntityTarget target;

    protected SetEntityOwnerFunction(LootContext.EntityTarget target, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.target = target;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof TamableAnimal tamableAnimal) {
            tamableAnimal.setOwnerUUID(pContext.getParam(target.getParam()).getUUID());
            tamableAnimal.setTame(true, false);
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
            return new SetEntityOwnerFunction(target, getConditions());
        }
    }

}
