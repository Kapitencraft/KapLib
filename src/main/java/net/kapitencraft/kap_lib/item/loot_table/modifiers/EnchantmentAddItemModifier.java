package net.kapitencraft.kap_lib.item.loot_table.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.helpers.LootTableHelper;
import net.kapitencraft.kap_lib.util.string_converter.converter.TextToDoubleConverter;
import net.kapitencraft.kap_lib.util.string_converter.param_storage.ParamStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class EnchantmentAddItemModifier extends AddItemModifier {
    public static final Codec<EnchantmentAddItemModifier> CODEC = RecordCodecBuilder.create(enchantmentAddItemModifierInstance ->
            addItemCodecStart(enchantmentAddItemModifierInstance).and(
                    ForgeRegistries.ENCHANTMENTS.getCodec().fieldOf("enchantment").forGetter(i -> i.enchantment)
            ).and(
                    Codec.STRING.fieldOf("provider").forGetter(i -> i.converter.getArgs())
            ).apply(enchantmentAddItemModifierInstance, EnchantmentAddItemModifier::new)
    );
    private final Enchantment enchantment;
    private final TextToDoubleConverter converter;
    @SuppressWarnings("all")
    protected EnchantmentAddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance, int maxAmount, Optional<CompoundTag> tag, Enchantment enchantment, String provider) {
        super(conditionsIn, item, chance, maxAmount, tag.orElse(null));
        this.converter = new TextToDoubleConverter(provider);
        this.enchantment = enchantment;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LivingEntity entity = LootTableHelper.getLivingSource(context);
        ItemStack stack = entity == null ? null : entity.getMainHandItem();
        if (stack != null) {
            double enchLevel = stack.getEnchantmentLevel(enchantment);
            addItem(generatedLoot::add, context, (float) (chance * converter.transfer(new ParamStorage<>(Map.of("ench", enchLevel)))));
        }
        return generatedLoot;
    }
}
