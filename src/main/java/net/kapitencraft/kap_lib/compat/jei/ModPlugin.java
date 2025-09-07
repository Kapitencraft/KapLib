package net.kapitencraft.kap_lib.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ModPlugin implements IModPlugin {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(RecipeTypes.CRAFTING, manager.getAllRecipesFor(ExtraRecipeTypes.ARMOR_RECIPE.get())
                .stream()
                .flatMap(holder -> {
                    ResourceLocation location = holder.id();
                    List<RecipeHolder<CraftingRecipe>> list = new ArrayList<>();
                    for (Map.Entry<ArmorRecipe.ArmorType, CraftingRecipe> entry : holder.value().getAll().entrySet()) {
                        list.add(new RecipeHolder<>(location.withSuffix("_" + entry.getKey().getSerializedName()), entry.getValue()));
                    }
                    return list.stream();
                }).toList()
        );
    }

    @Override
    public ResourceLocation getPluginUid() {
        return KapLibMod.res("plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableCraftingRecipeCategory category = registration.getCraftingCategory();
        category.addExtension(UpgradeItemRecipe.class, new UpgradeItemExtension());
    }

}
