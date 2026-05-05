package net.kapitencraft.kap_lib.recipe;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.recipe.registry.ExtraRecipeTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(RecipeModule.MODULE_ID)
public class RecipeModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_recipe";

    public RecipeModule(IEventBus modEventBus, ModContainer container) {
        ExtraRecipeTypes.REGISTRY.register(modEventBus);
    }
}
