package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnPool;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class AddPassengersFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<AddPassengersFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            SpawnPool.CODEC.fieldOf("passengers").forGetter(f -> f.pool)
    ).and(commonFields(i).t1()).apply(i, AddPassengersFunction::new));

    private final SpawnPool pool;

    protected AddPassengersFunction(SpawnPool pool, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.pool = pool;
    }

    public static Builder builder(SpawnPool.Builder builder) {
        return new Builder(builder);
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.getIndirectPassengers();
        pool.addRandomEntities(entity -> {
            entity.startRiding(pEntity, true);
        }, pContext);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.ADD_PASSENGERS.get();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final SpawnPool builder;

        public Builder(SpawnPool.Builder builder) {
            this.builder = builder.build();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new AddPassengersFunction(builder, getConditions());
        }
    }


}
