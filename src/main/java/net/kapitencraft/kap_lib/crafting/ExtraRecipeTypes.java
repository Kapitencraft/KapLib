package net.kapitencraft.kap_lib.crafting;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.crafting.serializers.ArmorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraRecipeTypes {
    DeferredRegister<RecipeType<?>> REGISTRY = KapLibMod.registry(Registries.RECIPE_TYPE);

    Supplier<RecipeType<ArmorRecipe>> ARMOR_RECIPE = register("armor");

    static <T extends Recipe<?>> Supplier<RecipeType<T>> register(final String name) {
        return REGISTRY.register(name, ()-> new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }
}