package net.kapitencraft.kap_lib.data_gen.abst.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class ArmorRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
    private final Map<ArmorItem.Type, ? extends RegistryObject<? extends Item>> items;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private Ingredient material;

    private ArmorRecipeBuilder(Map<ArmorItem.Type, ? extends RegistryObject<? extends Item>> items) {
        this.items = items;
    }

    public static ArmorRecipeBuilder create(Map<ArmorItem.Type, ? extends RegistryObject<? extends Item>> items) {
        return new ArmorRecipeBuilder(items);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String pCriterionName, @NotNull CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        group = pGroupName;
        return this;
    }

    public RecipeBuilder material(Ingredient ingredient) {
        this.material = ingredient;
        return this;
    }

    public RecipeBuilder material(Item item) {
        this.unlockedBy(getHasName(item), has(item));
        return this.material(Ingredient.of(item));
    }

    public RecipeBuilder material(RegistryObject<? extends Item> registryObject) {
        return this.material(registryObject.get());
    }

    @Override
    public @NotNull Item getResult() {
        return items.values().iterator().next().get(); //oh god
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        KapLibMod.LOGGER.warn("call to Armor Recipe save without recipe location definition");
        RecipeBuilder.super.save(pFinishedRecipeConsumer);
    }

    protected static String getHasName(ItemLike pItemLike) {
        return "has_" + getItemName(pItemLike);
    }

    protected static String getItemName(ItemLike pItemLike) {
        return BuiltInRegistries.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicates);
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(
                pRecipeId,
                this.advancement,
                pRecipeId.withPrefix("recipes/combat/"),
                this.group,
                this.material,
                this.items
                )
        );
    }

    private static class Result extends CraftingResult {
        private final ResourceLocation id;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final String group;
        private final Ingredient material;
        private final Map<ArmorItem.Type, ? extends RegistryObject<? extends Item>> items;

        private Result(ResourceLocation id, Advancement.Builder advancement, ResourceLocation advancementId, String group, Ingredient material, Map<ArmorItem.Type, ? extends RegistryObject<? extends Item>> items) {
            super(CraftingBookCategory.EQUIPMENT);
            this.id = id;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.group = group;
            this.material = material;
            this.items = items;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ExtraRecipeSerializers.ARMOR.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            super.serializeRecipeData(pJson);

            pJson.addProperty("group", this.group);
            pJson.add("material", this.material.toJson());

            serializeItemDefinition(pJson);
        }

        private void serializeItemDefinition(JsonObject object) {
            boolean saveAsString = true;
            String typeDef = null;
            for (Map.Entry<ArmorItem.Type, ? extends RegistryObject<? extends Item>> entry : this.items.entrySet()) {
                ArmorRecipe.ArmorType type = ArmorRecipe.ArmorType.fromEquipmentSlot(entry.getKey());
                ResourceLocation location = entry.getValue().getId();
                String typeSuffix = "_" + type.getSerializedName();
                if (!location.getPath().endsWith(typeSuffix)) saveAsString = false;
                if (saveAsString && typeDef == null) typeDef = location.withPath(s -> s.substring(0, s.length() - typeSuffix.length())).toString();

            }

            if (saveAsString) {
                object.addProperty("results", typeDef);
            } else {
                JsonArray array = new JsonArray();
                this.items.values().stream().map(RegistryObject::getId).map(ResourceLocation::toString).forEach(array::add);
                object.add("results", array);
            }
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}
