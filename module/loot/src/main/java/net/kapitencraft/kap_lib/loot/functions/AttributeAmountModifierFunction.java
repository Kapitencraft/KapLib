package net.kapitencraft.kap_lib.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemFunctions;
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

public class AttributeAmountModifierFunction extends LootItemConditionalFunction {
    public static final MapCodec<AttributeAmountModifierFunction> CODEC = RecordCodecBuilder.mapCodec(attributeAmountModifierFunctionInstance ->
            commonFields(attributeAmountModifierFunctionInstance)
                    .and(BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(i -> i.modifier))
                    .and(Formulas.CODEC.fieldOf("formula").forGetter(i -> i.formula))
                    .apply(attributeAmountModifierFunctionInstance, AttributeAmountModifierFunction::new)
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
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return ExtraLootItemFunctions.ATTRIBUTE_MODIFIER.get();
    }
}
