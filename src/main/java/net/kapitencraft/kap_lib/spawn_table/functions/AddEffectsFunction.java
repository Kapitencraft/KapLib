package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class AddEffectsFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<AddEffectsFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(f -> f.effects)
    ).and(commonFields(i).t1()).apply(i, AddEffectsFunction::new));

    public static final JsonSerializer<List<MobEffectInstance>> EFFECT_SERIALIZER = new JsonSerializer<>(ExtraCodecs.EFFECT.listOf(), List::of);

    private final List<MobEffectInstance> effects;

    protected AddEffectsFunction(List<MobEffectInstance> effects, List<LootItemCondition> pPredicates) {
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

        public Builder withEffect(Holder<MobEffect> effect, int duration) {
            return this.withEffect(new MobEffectInstance(effect, duration));
        }

        public Builder withEffect(Holder<MobEffect> effect, int duration, int amplifier) {
            return this.withEffect(new MobEffectInstance(effect, duration, amplifier));
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new AddEffectsFunction(effects, getConditions());
        }
    }
}
