package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SetMotionFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetMotionFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Vec3.CODEC.fieldOf("motion").forGetter(f -> f.motion)
    ).and(commonFields(i).t1()).apply(i, SetMotionFunction::new));

    private final Vec3 motion;
    protected SetMotionFunction(Vec3 motion, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.motion = motion;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        pEntity.setDeltaMovement(motion);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_MOTION.get();
    }
}
