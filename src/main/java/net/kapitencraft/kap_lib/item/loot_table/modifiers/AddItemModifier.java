package net.kapitencraft.kap_lib.item.loot_table.modifiers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.helpers.LootTableHelper;
import net.kapitencraft.kap_lib.helpers.RNGHelper;
import net.kapitencraft.kap_lib.item.loot_table.IConditional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class AddItemModifier extends ModLootModifier implements IConditional {
    protected static <T extends AddItemModifier> Products.P5<RecordCodecBuilder.Mu<T>, LootItemCondition[], Item, Float, Integer, Optional<DataComponentPatch>> addItemCodecStart(RecordCodecBuilder.Instance<T> instance) {
        return codecStart(instance)
                .and(BuiltInRegistries.ITEM.byNameCodec()
                        .fieldOf("item").forGetter(m -> m.item)
                ).and(Codec.FLOAT
                        .fieldOf("chance").forGetter(m -> m.chance)
                ).and(Codec.INT
                        .optionalFieldOf("maxAmount", 1).forGetter(m -> m.maxAmount)
                ).and(DataComponentPatch.CODEC.optionalFieldOf("nbt").forGetter(m -> Optional.ofNullable(m.components)));
    }
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> addItemCodecStart(inst).apply(inst, (conditionsIn, item1, chance1, maxAmount1, tagOptional) -> new AddItemModifier(conditionsIn, item1, chance1, maxAmount1, tagOptional.orElse(null))));
    final Item item;
    final int maxAmount;
    float chance;
    @Nullable
    final DataComponentPatch components;

    protected AddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance, int maxAmount, @Nullable DataComponentPatch components) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
        this.maxAmount = maxAmount;
        this.components = components;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        addItem(generatedLoot::add, context, chance);
        return generatedLoot;
    }

    public void addItem(Consumer<ItemStack> consumer, LootContext context, float chance) {
        ItemStack stack = RNGHelper.calculateAndDontDrop(item, maxAmount, LootTableHelper.getLivingSource(context), chance);
        if (components != null)
            stack.applyComponentsAndValidate(this.components);
        if (stack != ItemStack.EMPTY) {
            consumer.accept(stack);
        }
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}