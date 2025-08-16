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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class RaiderPropertiesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<RaiderPropertiesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("canJoinRaid", false).forGetter(f -> f.canJoinRaid),
            Codec.BOOL.optionalFieldOf("celebrating", false).forGetter(f -> f.celebrating)
    ).and(commonFields(i).t1()).apply(i, RaiderPropertiesFunction::new));

    private final Boolean canJoinRaid, celebrating;

    protected RaiderPropertiesFunction(Boolean canJoinRaid, Boolean celebrating, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.canJoinRaid = canJoinRaid;
        this.celebrating = celebrating;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Raider raider) {
            if (canJoinRaid != null) raider.setCanJoinRaid(canJoinRaid);
            if (celebrating != null) raider.setCelebrating(celebrating);
        } else logWrongType("Raider", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.RAIDER_PROPERTIES.get();
    }
}
