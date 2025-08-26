package net.kapitencraft.kap_lib.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@JeiPlugin
public class ModPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return KapLibMod.res("jei_plugin");
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(RecipeTypes.CRAFTING, manager.getAllRecipesFor(ExtraRecipeTypes.ARMOR_RECIPE.get()).stream()
                .map(ArmorRecipe::getAll).flatMap(Collection::stream).map(CraftingRecipe.class::cast).toList()
        );

    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableRecipeCategory<CraftingRecipe, ICraftingCategoryExtension> category = registration.getCraftingCategory();
        category.addCategoryExtension(UpgradeItemRecipe.class, UpgradeItemExtension::new);
    }

}
