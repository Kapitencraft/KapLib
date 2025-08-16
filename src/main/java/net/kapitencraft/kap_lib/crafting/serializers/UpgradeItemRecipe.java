package net.kapitencraft.kap_lib.crafting.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class UpgradeItemRecipe extends CustomRecipe {
    private final Ingredient source;
    private final Ingredient upgradeItem;
    private final ItemStack result;
    private final String group;
    private final CraftType type;

    public UpgradeItemRecipe(CraftingBookCategory bookCategory, Ingredient source, Ingredient upgradeItem, ItemStack result, String group, CraftType type) {
        super(bookCategory);
        this.source = source;
        this.upgradeItem = upgradeItem;
        this.result = result;
        this.group = group;
        this.type = type;
    }

    @Override
    public boolean matches(@NotNull CraftingInput craftingContainer, @NotNull Level level) {
        List<ItemStack> required = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (type.test(x, y)) {
                    required.add(craftingContainer.getItem(x + y * 3));
                }
            }
        }
        return source.test(craftingContainer.getItem(4)) && required.stream().allMatch(upgradeItem);
    }

    @Override
    public ItemStack assemble(CraftingInput pContainer, HolderLookup.Provider pRegistryAccess) {
        ItemStack source = pContainer.getItem(4);
        ItemStack result = this.result.copy(); //TODO data components
        return result;
    }

    public Ingredient getUpgradeItem() {
        return upgradeItem;
    }

    public Ingredient getSource() {
        return source;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider pRegistryAccess) {
        return result;
    }

    public ItemStack getResult() {
        return result;
    }

    public CraftType getCraftType() {
        return type;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i == 3 && j == 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ExtraRecipeSerializers.UPGRADE_ITEM.value();
    }

    private interface PositionPredicate {
        boolean apply(int x, int y);
    }

    public enum CraftType implements StringRepresentable, BiPredicate<Integer, Integer> {
        EIGHT("eight", (x, y) -> x + 3*y != 4),
        FOUR("four", (x, y) -> EIGHT.test(x, y) && (x == 1 && y % 2 == 0) || (y == 1 && x % 2 == 0)),
        FOUR_DIAGONAL("four_diagonal", (x, y) -> EIGHT.test(x, y) && (x % 2 == 0 && y % 2 == 0));

        static final EnumCodec<CraftType> CODEC = StringRepresentable.fromEnum(CraftType::values);
        private final String name;
        private final PositionPredicate applyPredicate;

        CraftType(String name, PositionPredicate applyPredicate) {
            this.name = name;
            this.applyPredicate = applyPredicate;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public boolean test(Integer integer, Integer integer2) {
            return applyPredicate.apply(integer, integer2);
        }
    }

    public static class Serializer implements RecipeSerializer<UpgradeItemRecipe> {
        public static final MapCodec<UpgradeItemRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category),
                Ingredient.CODEC.fieldOf("source").forGetter(UpgradeItemRecipe::getSource),
                Ingredient.CODEC.fieldOf("material").forGetter(UpgradeItemRecipe::getUpgradeItem),
                ItemStack.CODEC.fieldOf("result").forGetter(UpgradeItemRecipe::getResult),
                Codec.STRING.optionalFieldOf("group", "").forGetter(UpgradeItemRecipe::getGroup),
                CraftType.CODEC.fieldOf("craft_type").forGetter(UpgradeItemRecipe::getCraftType)
                ).apply(i, UpgradeItemRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, UpgradeItemRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static UpgradeItemRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
            String s = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient source = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            Ingredient upgradeItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
            CraftType type = buf.readEnum(CraftType.class);
            return new UpgradeItemRecipe(category, source, upgradeItem, stack, s, type);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, UpgradeItemRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.source);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.upgradeItem);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeEnum(recipe.type);
        }

        @Override
        public MapCodec<UpgradeItemRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, UpgradeItemRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}