package net.kapitencraft.kap_lib.mixin.classes.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    @Shadow protected Minecraft minecraft;
    @Unique
    private Recipe<?> lastRecipe = null;
    @Unique
    private RecipeCollection lastCollection = null;
    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void addRefill(int key, int x, int y, CallbackInfoReturnable<Boolean> cir) {
        if (key != ' ') return;
        MultiPlayerGameMode gameMode = this.minecraft.gameMode;
        Player player = this.minecraft.player;
         if (gameMode != null && player != null && lastRecipe != null && lastCollection != null && lastCollection.isCraftable(lastRecipe)) {
            gameMode.handlePlaceRecipe(player.containerMenu.containerId, lastRecipe, Screen.hasShiftDown());
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePlaceRecipe(ILnet/minecraft/world/item/crafting/Recipe;Z)V"))
    private void setLastRecipe(double p_100294_, double p_100295_, int p_100296_, CallbackInfoReturnable<Boolean> cir) {
        lastRecipe = this.recipeBookPage.getLastClickedRecipe();
        lastCollection = this.recipeBookPage.getLastClickedRecipeCollection();
    }
}
