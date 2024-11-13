package net.kapitencraft.kap_lib.crafting.serializers;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class UpgradeItemRecipe extends CustomRecipe {
    private final Ingredient toUpgrade;
    private final Ingredient upgradeItem;
    private final ItemStack result;
    private final String group;
    private final CraftType type;

    public UpgradeItemRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_, Ingredient toUpgrade, Ingredient upgradeItem, ItemStack result, String group, CraftType type) {
        super(p_252125_, p_249010_);
        this.toUpgrade = toUpgrade;
        this.upgradeItem = upgradeItem;
        this.result = result;
        this.group = group;
        this.type = type;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer craftingContainer, @NotNull Level level) {
        List<ItemStack> required = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (type.test(x, y)) {
                    required.add(craftingContainer.getItem(x + y * 3));
                }
            }
        }
        return toUpgrade.test(craftingContainer.getItem(4)) && required.stream().allMatch(upgradeItem);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack source = pContainer.getItem(4);
        ItemStack result = this.result.copy();
        result.setTag(source.getTag());
        return result;
    }

    public Ingredient getUpgradeItem() {
        return upgradeItem;
    }

    public Ingredient getToUpgrade() {
        return toUpgrade;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
    public @NotNull RecipeSerializer<UpgradeItemRecipe> getSerializer() {
        return ModRecipeSerializers.UPGRADE_ITEM.get();
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

        @Override
        public @NotNull UpgradeItemRecipe fromJson(@NotNull ResourceLocation location, @NotNull JsonObject jsonObject) {
            String s = GsonHelper.getAsString(jsonObject, "group", "");
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CraftingBookCategory.MISC);
            Ingredient source = Ingredient.fromJson(jsonObject.get("source"));
            Ingredient upgradeItem = Ingredient.fromJson(jsonObject.get("material"));
            ItemStack result = ShapedRecipe.itemStackFromJson(jsonObject.getAsJsonObject("result"));
            CraftType type = CraftType.CODEC.byName(GsonHelper.getAsString(jsonObject, "craft_type"));
            return new UpgradeItemRecipe(location, craftingbookcategory, source, upgradeItem, result, s, type);
        }

        @Override
        public @Nullable UpgradeItemRecipe fromNetwork(@NotNull ResourceLocation location, @NotNull FriendlyByteBuf buf) {
            String s = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient source = Ingredient.fromNetwork(buf);
            Ingredient upgradeItem = Ingredient.fromNetwork(buf);
            ItemStack stack = buf.readItem();
            CraftType type = buf.readEnum(CraftType.class);
            return new UpgradeItemRecipe(location, category, source, upgradeItem, stack, s, type);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, UpgradeItemRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category());
            recipe.toUpgrade.toNetwork(buf);
            recipe.upgradeItem.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeEnum(recipe.type);
        }
    }
}