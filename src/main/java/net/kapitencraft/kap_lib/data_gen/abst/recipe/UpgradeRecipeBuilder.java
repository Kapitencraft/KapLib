package net.kapitencraft.kap_lib.data_gen.abst.recipe;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UpgradeRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final UpgradeItemRecipe.CraftType type;
    private final Item result;
    private final int count;
    private Ingredient source, material;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;

    public UpgradeRecipeBuilder(RecipeCategory pCategory, UpgradeItemRecipe.CraftType type, ItemLike pResult, int pCount) {
        this.category = pCategory;
        this.type = type;
        this.result = pResult.asItem();
        this.count = pCount;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static UpgradeRecipeBuilder create(RecipeCategory pCategory, UpgradeItemRecipe.CraftType type, ItemLike pResult) {
        return create(pCategory, type, pResult, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static UpgradeRecipeBuilder create(RecipeCategory pCategory, UpgradeItemRecipe.CraftType type, ItemLike pResult, int pCount) {
        return new UpgradeRecipeBuilder(pCategory, type, pResult, pCount);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public UpgradeRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public UpgradeRecipeBuilder source(Ingredient ingredient) {
        this.source = ingredient;
        return this;
    }

    public UpgradeRecipeBuilder source(DeferredItem<? extends Item> registryObject) {
        this.source(Ingredient.of(registryObject.get()));
        return this;
    }

    public UpgradeRecipeBuilder source(Item item) {
        this.source(Ingredient.of(item));
        return this;
    }

    public UpgradeRecipeBuilder material(Ingredient ingredient) {
        this.material = ingredient;
        return this;
    }

    public UpgradeRecipeBuilder material(DeferredItem<? extends Item> registryObject) {
        this.material(Ingredient.of(registryObject.get()));
        return this;
    }

    public UpgradeRecipeBuilder material(Item item) {
        this.material(Ingredient.of(item));
        return this;
    }

    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ensureValid(id);
        recipeOutput.accept(id, new UpgradeItemRecipe(RecipeBuilder.determineBookCategory(this.category), source, material, new ItemStack(result), group, type), this.advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.material == null) throw new IllegalStateException("material not defined in Upgrade recipe " + pId + "!");
        if (this.source == null) throw new IllegalStateException("source not defined in Upgrade recipe " + pId + "!");
    }
}