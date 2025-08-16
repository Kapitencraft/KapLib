package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class SetFacingFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetFacingFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("pitch").forGetter(f -> f.rot.x),
            Codec.FLOAT.fieldOf("yaw").forGetter(f -> f.rot.y)
    ).and(commonFields(i).t1()).apply(i, SetFacingFunction::fromCodec));

    private static SetFacingFunction fromCodec(float pitch, float yaw, List<LootItemCondition> lootItemConditions) {
        return new SetFacingFunction(lootItemConditions, new Vec2(pitch, yaw));
    }

    private final Vec2 rot;

    protected SetFacingFunction(List<LootItemCondition> pPredicates, Vec2 rot) {
        super(pPredicates);
        this.rot = rot;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setXRot(rot.x);
        pEntity.setYRot(rot.y);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.SET_FACING.get();
    }
}
