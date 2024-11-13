package net.kapitencraft.kap_lib.item.loot_table.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraLootItemConditions;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class LootTableTypeCondition extends BaseCondition {
    private static final LootTableTypeCondition EMPTY = new LootTableTypeCondition(null);
    private static final Codec<LootTableTypeCondition> CODEC = RecordCodecBuilder.create(lootTableTypeConditionInstance ->
            lootTableTypeConditionInstance.group(
                    TagKeyCondition.Type.CODEC.fieldOf("type").forGetter(i -> i.type)
            ).apply(lootTableTypeConditionInstance, LootTableTypeCondition::new)
    );
    private final TagKeyCondition.Type type;

    public LootTableTypeCondition(TagKeyCondition.Type type) {
        this.type = type;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return ExtraLootItemConditions.TYPE.get();
    }

    @Override
    public boolean test(LootContext context) {
        return type.is(context);
    }

    public static final JsonSerializer<LootTableTypeCondition> SERIALIZER = new JsonSerializer<>(CODEC, () -> EMPTY);
}