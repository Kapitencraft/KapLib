package net.kapitencraft.kap_lib.loot.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.core.string_converter.converter.TextToDoubleConverter;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantmentAddItemModifier extends AddItemModifier {
    public static final MapCodec<EnchantmentAddItemModifier> CODEC = RecordCodecBuilder.mapCodec(enchantmentAddItemModifierInstance ->
            addItemCodecStart(enchantmentAddItemModifierInstance).and(
                    Enchantment.CODEC.fieldOf("enchantment").forGetter(i -> i.enchantment)
            ).and(
                    Codec.STRING.fieldOf("provider").forGetter(i -> i.chanceProvider.getArgs())
            ).apply(enchantmentAddItemModifierInstance, EnchantmentAddItemModifier::new)
    );
    private final Holder<Enchantment> enchantment;
    private final TextToDoubleConverter chanceProvider;

    @SuppressWarnings("all")
    protected EnchantmentAddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance, int maxAmount, Optional<DataComponentPatch> tag, Holder<Enchantment> enchantment, String provider) {
        super(conditionsIn, item, chance, maxAmount, tag.orElse(null));
        this.chanceProvider = new TextToDoubleConverter(provider);
        this.enchantment = enchantment;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity param = context.getParam(LootContextParams.ATTACKING_ENTITY);
        if (!(param instanceof LivingEntity living)) return generatedLoot;
        ItemStack stack = living.getMainHandItem();
        double enchLevel = stack.getEnchantmentLevel(enchantment);
        addItem(generatedLoot::add, context, (float) (chance * chanceProvider.transfer(new ParamStorage<>(Map.of("ench", enchLevel)))));
        return generatedLoot;
    }

    public static class Builder {
        private final List<LootItemCondition> conditions = new ArrayList<>();
        private Item item;
        private float chance;
        private int maxAmount = 1;
        private DataComponentPatch tag;
        private Holder<Enchantment> enchantment;
        private String provider;

        public Builder withCondition(LootItemCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder setItem(Item item) {
            this.item = item;
            return this;
        }

        public Builder setChance(float chance) {
            this.chance = chance;
            return this;
        }

        public Builder setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public Builder setTag(DataComponentPatch tag) {
            this.tag = tag;
            return this;
        }

        public Builder setEnchantment(Holder<Enchantment> enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }

        public EnchantmentAddItemModifier build() {
            return new EnchantmentAddItemModifier(conditions.toArray(LootItemCondition[]::new), item, chance, maxAmount, Optional.ofNullable(tag), enchantment, provider);
        }
    }
}
