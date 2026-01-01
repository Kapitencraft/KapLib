package net.kapitencraft.kap_lib.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Function;

public interface IConditional {

    static <T extends IConditional> MapCodec<T> simpleCodec(Function<LootItemCondition[], T> function) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                LootModifier.LOOT_CONDITIONS_CODEC.optionalFieldOf("conditions", new LootItemCondition[0]).forGetter(IConditional::getConditions)
        ).apply(instance, function));
    }

    LootItemCondition[] getConditions();
}
