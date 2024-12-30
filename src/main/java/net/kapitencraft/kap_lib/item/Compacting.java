package net.kapitencraft.kap_lib.item;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Compacting {
    private static final Map<Item, Result> resultCache = new HashMap<>();
    private static RecipeManager manager;

    public static Result tryCompact(Item in, Level level) {
        if (resultCache.containsKey(in)) return resultCache.get(in);

        Optional<CraftingRecipe> smallRecipe = manager.getRecipeFor(RecipeType.CRAFTING, new Container(true, in), level);
        Optional<CraftingRecipe> largeRecipe = manager.getRecipeFor(RecipeType.CRAFTING, new Container(false, in), level);

        Result result = new Result(
                smallRecipe.map(craftingRecipe -> craftingRecipe.getResultItem(level.registryAccess())).orElse(null),
                largeRecipe.map(craftingRecipe -> craftingRecipe.getResultItem(level.registryAccess())).orElse(null)
        );
        resultCache.put(in, result);

        return result;
    }

    public static class Result {
        private final @Nullable ItemStack small, large;

        private Result(@Nullable ItemStack small, @Nullable ItemStack large) {
            this.small = small;
            this.large = large;
        }

        public boolean successful() {
            return small != null || large != null;
        }
    }

    private static class Container implements CraftingContainer {
        private final boolean small;
        private final ItemStack val;

        private Container(boolean small, Item in) {
            this.small = small;
            this.val = new ItemStack(in);
        }

        @Override
        public int getWidth() {
            return small ? 2 : 3;
        }

        @Override
        public int getHeight() {
            return small ? 2 : 3;
        }

        @Override
        public List<ItemStack> getItems() {
            return List.of();
        }

        @Override
        public int getContainerSize() {
            return small ? 4 : 9;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getItem(int pSlot) {
            return val;
        }

        @Override
        public ItemStack removeItem(int pSlot, int pAmount) {
            return null;
        }

        @Override
        public ItemStack removeItemNoUpdate(int pSlot) {
            return null;
        }

        @Override
        public void setItem(int pSlot, ItemStack pStack) {

        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(Player pPlayer) {
            return false;
        }

        @Override
        public void clearContent() {

        }

        @Override
        public void fillStackedContents(StackedContents pContents) {

        }
    }
}
