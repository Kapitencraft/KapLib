package net.kapitencraft.kap_lib.crafting;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ModRecipeTypes {
    DeferredRegister<RecipeType<?>> REGISTRY = KapLibMod.registry(ForgeRegistries.RECIPE_TYPES);

    RegistryObject<RecipeType<ArmorRecipe>> ARMOR_RECIPE = register("armor");

    static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(final String name) {
        return REGISTRY.register(name, ()-> new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }
}