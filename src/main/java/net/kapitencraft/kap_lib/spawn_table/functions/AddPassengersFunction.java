package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Function;

public class AddPassengersFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<AddPassengersFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            SpawnTable.GATHER_CODEC.fieldOf("passengers").forGetter(f -> f.passengers)
    ).and(commonFields(i).t1()).apply(i, AddPassengersFunction::new));

    private final Either<ResourceKey<SpawnTable>, SpawnTable> passengers;

    protected AddPassengersFunction(Either<ResourceKey<SpawnTable>, SpawnTable> passengers, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.passengers = passengers;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.getIndirectPassengers();
        SpawnTable table = passengers.map(k -> pContext.getResolver().get(SpawnTableRegistries.Keys.SPAWN_TABLES, k).map(Holder::value).orElse(SpawnTable.EMPTY), Function.identity());
        table.getRandomEntitiesRaw(pContext, entity -> {
            entity.startRiding(pEntity, true);
        });
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.ADD_PASSENGERS.get();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private Either<ResourceKey<SpawnTable>, SpawnTable> builder;

        public Builder() {
        }

        public Builder withTable(ResourceKey<SpawnTable> key) {
            builder = Either.left(key);
            return this;
        }

        public Builder withTable(SpawnTable table) {
            builder = Either.right(table);
            return this;
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
