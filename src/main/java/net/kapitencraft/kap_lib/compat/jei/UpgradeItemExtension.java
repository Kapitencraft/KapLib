package net.kapitencraft.kap_lib.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpgradeItemExtension implements ICraftingCategoryExtension {
    private final UpgradeItemRecipe recipe;

    public UpgradeItemExtension(UpgradeItemRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        craftingGridHelper.createAndSetInputs(builder, getIng(), 3, 3);
        craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.getResult()));
    }

    private List<List<ItemStack>> getIng() {
        List<List<ItemStack>> lists = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == 1 && y == 1)
                    lists.add(List.of(recipe.getToUpgrade().getItems()));
                else if (recipe.getCraftType().test(x, y))
                    lists.add(List.of(recipe.getUpgradeItem().getItems()));
                else lists.add(List.of());
            }
        }
        return lists;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return recipe.getId();
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public int getWidth() {
        return 3;
    }
}
