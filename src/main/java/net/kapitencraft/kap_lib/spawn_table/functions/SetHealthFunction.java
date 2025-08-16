package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetHealthFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetHealthFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("health").forGetter(f -> f.health),
            Codec.INT.optionalFieldOf("absorption", 0).forGetter(f -> f.absorption)
    ).and(commonFields(i).t1()).apply(i, SetHealthFunction::new));

    private final int health;
    private final int absorption;

    protected SetHealthFunction(int health, int absorption, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.health = health;
        this.absorption = absorption;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof LivingEntity living) {
            living.setHealth(health);
            if (absorption != -1) living.setAbsorptionAmount(absorption);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.SET_HEALTH.get();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final int health;
        private int absorption = -1;

        public Builder(int health) {
            this.health = health;
        }

        public Builder withAbsorption(int absorption) {
            this.absorption = absorption;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetHealthFunction(health, absorption, getConditions());
        }
    }
}
