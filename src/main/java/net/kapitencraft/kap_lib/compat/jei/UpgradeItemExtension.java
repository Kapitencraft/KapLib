package net.kapitencraft.kap_lib.compat.jei;

import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class UpgradeItemExtension implements ICraftingCategoryExtension<UpgradeItemRecipe> {

    public UpgradeItemExtension() {
    }

    private List<List<ItemStack>> getIng(UpgradeItemRecipe recipe) {
        List<List<ItemStack>> lists = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == 1 && y == 1)
                    lists.add(List.of(recipe.getSource().getItems()));
                else if (recipe.getCraftType().test(x, y))
                    lists.add(List.of(recipe.getUpgradeItem().getItems()));
                else lists.add(List.of());
            }
        }
        return lists;
    }

    @Override
    public int getWidth(RecipeHolder<UpgradeItemRecipe> recipeHolder) {
        return 3;
    }

    @Override
    public int getHeight(RecipeHolder<UpgradeItemRecipe> recipeHolder) {
        return 3;
    }
}
