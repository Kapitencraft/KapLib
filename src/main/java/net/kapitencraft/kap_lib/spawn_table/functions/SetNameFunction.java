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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetNameFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetNameFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(f -> f.name)
            ).and(commonFields(i).t1()).apply(i, SetNameFunction::new)
    );

    private final Component name;

    public SetNameFunction(Component name, List<LootItemCondition> conditions) {
        super(conditions);
        this.name = name;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
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
            return new SetNameFunction(name, this.getConditions());
        }
    }
}
