package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetFireFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetFireFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("visual", true).forGetter(f -> f.visualFire),
            Codec.INT.fieldOf("duration").forGetter(f -> f.duration)
    ).and(commonFields(i).t1()).apply(i, SetFireFunction::new));

    private final Boolean visualFire;
    private final int duration;

    protected SetFireFunction(Boolean visualFire, int duration, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.visualFire = visualFire;
        this.duration = duration;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setRemainingFireTicks(duration);
        if (visualFire != null) pEntity.hasVisualFire = visualFire;
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_FIRE_DURATION.get();
    }
}
