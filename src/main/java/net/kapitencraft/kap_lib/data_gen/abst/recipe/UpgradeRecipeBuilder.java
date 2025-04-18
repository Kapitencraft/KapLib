package net.kapitencraft.kap_lib.data_gen.abst.recipe;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UpgradeRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
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

    public UpgradeRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
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

    public UpgradeRecipeBuilder source(RegistryObject<? extends Item> registryObject) {
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

    public UpgradeRecipeBuilder material(RegistryObject<? extends Item> registryObject) {
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

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(
                pRecipeId,
                this.result,
                this.count,
                this.group == null ? "" : this.group,
                determineBookCategory(this.category),
                this.type,
                this.source,
                this.material,
                this.advancement,
                pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.material == null) throw new IllegalStateException("material not defined in Upgrade recipe " + pId + "!");
        if (this.source == null) throw new IllegalStateException("source not defined in Upgrade recipe " + pId + "!");
    }

    private static class Result extends CraftingResult {
        private final ResourceLocation id;
        private final UpgradeItemRecipe.CraftType type;
        private final Item result;
        private final int count;
        private final String group;
        private final Ingredient source, material;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        private Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, CraftingBookCategory pCategory, UpgradeItemRecipe.CraftType type, Ingredient source, Ingredient material, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            super(pCategory);
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.group = pGroup;
            this.type = type;
            this.source = source;
            this.material = material;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        public void serializeRecipeData(JsonObject pJson) {
            super.serializeRecipeData(pJson);

            pJson.addProperty("craft_type", this.type.getSerializedName());

            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            pJson.add("source", this.source.toJson());
            pJson.add("material", this.material.toJson());

            JsonObject object = new JsonObject();
            object.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                object.addProperty("count", this.count);
            }

            pJson.add("result", object);
        }

        public RecipeSerializer<?> getType() {
            return ExtraRecipeSerializers.UPGRADE_ITEM.get();
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}