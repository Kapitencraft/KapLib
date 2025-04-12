package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class AddEffectsFunction extends SpawnEntityConditionalFunction {
    public static final JsonSerializer<List<MobEffectInstance>> EFFECT_SERIALIZER = new JsonSerializer<>(ExtraCodecs.EFFECT.listOf(), List::of);

    private final MobEffectInstance[] effects;

    protected AddEffectsFunction(LootItemCondition[] pPredicates, MobEffectInstance[] effects) {
        super(pPredicates);
        this.effects = effects;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof LivingEntity living) {
            for (MobEffectInstance effect : effects) {
                living.addEffect(effect);
            }
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.ADD_EFFECTS.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<AddEffectsFunction> {

        @Override
        public void serialize(JsonObject pJson, AddEffectsFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("effects", EFFECT_SERIALIZER.serialize(List.of(pFunction.effects)));
        }

        @Override
        public AddEffectsFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            MobEffectInstance[] effects = EFFECT_SERIALIZER.deserialize(pObject.get("effects")).toArray(MobEffectInstance[]::new);
            return new AddEffectsFunction(pConditions, effects);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {

        private Builder() {
        }

        private List<MobEffectInstance> effects = new ArrayList<>();

        public Builder withEffect(MobEffectInstance instance) {
            effects.add(instance);
            return this;
        }

        public Builder withEffect(MobEffect effect, int duration) {
            return this.withEffect(new MobEffectInstance(effect, duration));
        }

        public Builder withEffect(MobEffect effect, int duration, int amplifier) {
            return this.withEffect(new MobEffectInstance(effect, duration, amplifier));
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new AddEffectsFunction(getConditions(), effects.toArray(MobEffectInstance[]::new));
        }
    }
}
