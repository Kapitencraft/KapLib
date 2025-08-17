package net.kapitencraft.kap_lib.item.loot_table.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.item.loot_table.IConditional;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.ModLootModifier;
import net.kapitencraft.kap_lib.registry.ExtraLootItemFunctions;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class AttributeAmountModifierFunction extends LootItemConditionalFunction implements IConditional {
    private static final Codec<AttributeAmountModifierFunction> CODEC = RecordCodecBuilder.create(attributeAmountModifierFunctionInstance ->
            attributeAmountModifierFunctionInstance.group(
                    ModLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(i -> i.predicates),
                    BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(i -> i.modifier),
                    Formulas.CODEC.fieldOf("formula").forGetter(i -> i.formula)
            ).apply(attributeAmountModifierFunctionInstance, AttributeAmountModifierFunction::new)
    );

    private final Holder<Attribute> modifier;
    private final Formulas formula;
    private AttributeAmountModifierFunction(List<LootItemCondition> conditions, Holder<Attribute> attribute, Formulas formula) {
        super(conditions);
        this.modifier = attribute;
        this.formula = formula;
    }

    public Holder<Attribute> getModifier() {
        return modifier;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, LootContext lootContext) {
        Entity source = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (source instanceof LivingEntity living) {
            double amount = living.getAttributeValue(modifier);
            return formula.provide(stack, amount);
        }
        return stack;
    }

    @Override
    public List<LootItemCondition> getConditions() {
        return predicates;
    }

    public interface Formula {
        String getName();
        ItemStack provide(ItemStack stack, double d);
    }

    public enum Formulas implements Formula, StringRepresentable {
        DEFAULT("default", (stack, d) -> stack.copyWithCount((int) (stack.getCount() * (1 + d / 100))));

        public static final EnumCodec<Formulas> CODEC = StringRepresentable.fromEnum(Formulas::values);

        private final String name;
        private final BiFunction<ItemStack, Double, ItemStack> provider;

        Formulas(String name, BiFunction<ItemStack, Double, ItemStack> provider) {
            this.name = name;
            this.provider = provider;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ItemStack provide(ItemStack stack, double d) {
            return provider.apply(stack, d);
        }

        public static Formulas byName(String name) {
            return CODEC.byName(name, DEFAULT);
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    @Override
    public @NotNull LootItemFunctionType<?> getType() {
        return ExtraLootItemFunctions.ATTRIBUTE_MODIFIER.value();
    }

    public static final JsonSerializer<AttributeAmountModifierFunction> SERIALIZER = new JsonSerializer<>(CODEC);
}
