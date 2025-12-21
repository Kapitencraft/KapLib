package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.recipe.serializers.UpgradeItemRecipe;
import net.kapitencraft.kap_lib.recipe.datagen.UpgradeRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class TestRecipeProvider extends RecipeProvider {
    public TestRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(pOutput, registries);
    }

    protected void buildRecipes(RecipeOutput output) {
        UpgradeRecipeBuilder.create(RecipeCategory.MISC, UpgradeItemRecipe.CraftType.FOUR, Items.NETHER_STAR).material(Items.WITHER_SKELETON_SKULL).source(Items.HEART_OF_THE_SEA)
                .save(output, "test:test");
    }
}
