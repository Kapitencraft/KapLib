package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraRecipeSerializers {
    DeferredRegister<RecipeSerializer<?>> REGISTRY = KapLibMod.registry(Registries.RECIPE_SERIALIZER);

    Holder<RecipeSerializer<?>> UPGRADE_ITEM = REGISTRY.register("upgrade_item", UpgradeItemRecipe.Serializer::new);
    Holder<RecipeSerializer<?>> ARMOR = REGISTRY.register("armor", ArmorRecipe.Serializer::new);
}
