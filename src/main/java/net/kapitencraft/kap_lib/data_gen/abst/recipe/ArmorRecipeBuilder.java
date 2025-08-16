package net.kapitencraft.kap_lib.data_gen.abst.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArmorRecipeBuilder implements RecipeBuilder {
    private final Map<ArmorItem.Type, ? extends DeferredItem<? extends Item>> items;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private Ingredient material;

    private ArmorRecipeBuilder(Map<ArmorItem.Type, ? extends DeferredItem<? extends Item>> items) {
        this.items = items;
    }

    public static ArmorRecipeBuilder create(Map<ArmorItem.Type, ? extends DeferredItem<? extends Item>> items) {
        return new ArmorRecipeBuilder(items);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        advancement.addCriterion(name, criterion);
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

    public RecipeBuilder material(DeferredItem<? extends Item> registryObject) {
        return this.material(registryObject.get());
    }

    @Override
    public @NotNull Item getResult() {
        return items.values().iterator().next().get(); //oh god
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, new ArmorRecipe(material, convert(items), group), this.advancement.build(id.withPrefix("recipes/")));
    }

    private Map<ArmorRecipe.ArmorType, ItemStack> convert(Map<ArmorItem.Type, ? extends DeferredItem<? extends Item>> items) {
        return MapStream.of(items).mapKeys(ArmorRecipe.ArmorType::fromEquipmentSlot).mapValues(DeferredItem::asItem).mapValues(ItemStack::new).toMap();
    }


    protected static String getHasName(ItemLike pItemLike) {
        return "has_" + getItemName(pItemLike);
    }

    protected static String getItemName(ItemLike pItemLike) {
        return BuiltInRegistries.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    protected static Criterion<?> has(ItemLike pItemLike) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build()));
    }

    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicates) {
        return new InventoryChangeTrigger.TriggerInstance(Optional.empty(), new InventoryChangeTrigger.TriggerInstance.Slots(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY), List.of(pPredicates));
    }
}
