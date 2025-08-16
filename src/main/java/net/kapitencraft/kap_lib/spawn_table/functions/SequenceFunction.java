package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import java.util.List;
import java.util.function.BiFunction;

public class SequenceFunction implements SpawnEntityFunction {
    public static final MapCodec<SequenceFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_335342_ -> p_335342_.group(SpawnEntityFunctions.TYPED_CODEC.listOf().fieldOf("functions").forGetter(p_298431_ -> p_298431_.functions))
                .apply(p_335342_, SequenceFunction::new)
    );
    public static final Codec<SequenceFunction> INLINE_CODEC = SpawnEntityFunctions.TYPED_CODEC
        .listOf()
        .xmap(SequenceFunction::new, p_298862_ -> p_298862_.functions);
    private final List<SpawnEntityFunction> functions;
    private final BiFunction<Entity, SpawnContext, Entity> compositeFunction;

    private SequenceFunction(List<SpawnEntityFunction> functions) {
        this.functions = functions;
        this.compositeFunction = SpawnEntityFunctions.compose(functions);
    }

    public static SequenceFunction of(List<SpawnEntityFunction> functions) {
        return new SequenceFunction(List.copyOf(functions));
    }

    public Entity apply(Entity stack, SpawnContext context) {
        return this.compositeFunction.apply(stack, context);
    }

    /**
     * Validate that this object is used correctly according to the given ValidationContext.
     */
    @Override
    public void validate(ValidationContext context) {
        SpawnEntityFunction.super.validate(context);

        for (int i = 0; i < this.functions.size(); i++) {
            this.functions.get(i).validate(context.forChild(".function[" + i + "]"));
        }
    }

    @Override
    public SpawnEntityFunctionType<SequenceFunction> getType() {
        return SpawnEntityFunctions.SEQUENCE;
    }
}
