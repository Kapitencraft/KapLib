package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnEntity;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetExperienceValueFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetExperienceValueFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("value").forGetter(f -> f.value)
    ).and(commonFields(i).t1()).apply(i, SetExperienceValueFunction::new));

    private final int value;

    protected SetExperienceValueFunction(int value, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.value = value;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof ExperienceOrb orb) {
            orb.value = value;
        } else logWrongType("ExperienceOrb", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.SET_EXPERIENCE_VALUE.get();
    }

    public static Builder builder(int value) {
        return new Builder(value);
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final int value;

        public Builder(int value) {
            this.value = value;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetExperienceValueFunction(value, getConditions());
        }
    }
}
