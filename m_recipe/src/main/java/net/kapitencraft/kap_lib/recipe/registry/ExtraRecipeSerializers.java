package net.kapitencraft.kap_lib.recipe.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.recipe.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.recipe.serializers.UpgradeItemRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraRecipeSerializers {
    DeferredRegister<RecipeSerializer<?>> REGISTRY = LibConstants.registry(Registries.RECIPE_SERIALIZER);

    Holder<RecipeSerializer<?>> UPGRADE_ITEM = REGISTRY.register("upgrade_item", UpgradeItemRecipe.Serializer::new);
    Holder<RecipeSerializer<?>> ARMOR = REGISTRY.register("armor", ArmorRecipe.Serializer::new);
}
