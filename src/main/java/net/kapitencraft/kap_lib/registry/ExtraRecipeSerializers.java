package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.kapitencraft.kap_lib.crafting.serializers.UpgradeItemRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraRecipeSerializers {
    DeferredRegister<RecipeSerializer<?>> REGISTRY = KapLibMod.registry(ForgeRegistries.RECIPE_SERIALIZERS);

    RegistryObject<RecipeSerializer<UpgradeItemRecipe>> UPGRADE_ITEM = REGISTRY.register("upgrade_item", UpgradeItemRecipe.Serializer::new);
    RegistryObject<RecipeSerializer<ArmorRecipe>> ARMOR = REGISTRY.register("armor", ArmorRecipe.Serializer::new);
}
