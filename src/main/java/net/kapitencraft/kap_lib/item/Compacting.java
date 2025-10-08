package net.kapitencraft.kap_lib.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API for simple item compacting
 */
public class Compacting {
    private static final Map<Item, Result> resultCache = new HashMap<>();

    /**
     * attempts to get a compacted item of the given item. see {@link Result for more info}
     * @param in the item to compact
     * @param level the level to get the recipes from
     * @return the result of the operation
     */
    public static Result tryCompact(Item in, ServerLevel level) {
        if (resultCache.containsKey(in)) return resultCache.get(in);
        RecipeManager manager = level.getRecipeManager();

        Optional<CraftingRecipe> smallRecipe = manager.getRecipeFor(RecipeType.CRAFTING, new Container(true, in), level);
        Optional<CraftingRecipe> largeRecipe = manager.getRecipeFor(RecipeType.CRAFTING, new Container(false, in), level);

        Result result;
        if (smallRecipe.isEmpty() && largeRecipe.isEmpty())
            result = Result.EMPTY;
        else
            result = new Result(
                smallRecipe.map(craftingRecipe -> craftingRecipe.getResultItem(level.registryAccess())).orElse(null),
                largeRecipe.map(craftingRecipe -> craftingRecipe.getResultItem(level.registryAccess())).orElse(null)
            );
        resultCache.put(in, result);

        return result;
    }

    /**
     * result container for compacting processes
     */
    public static class Result {
        public static final Result EMPTY = new Result(null, null);

        private final @Nullable ItemStack small, large;

        private Result(@Nullable ItemStack small, @Nullable ItemStack large) {
            this.small = small;
            this.large = large;
        }

        /**
         * @return whether a compacted item could be found or not
         */
        public boolean successful() {
            return small != null || large != null;
        }

        /**
         * @return whether the compacted recipe is 2x2 (if present)
         */
        public boolean isSmall() {
            return successful() && small != null;
        }

        /**
         * @return the amount of items required to compact the item
         */
        public int getCountReq() {
            return small != null ? 4 : large != null ? 9 : -1;
        }

        /**
         * @return the resulting item, if any
         */
        public @Nullable ItemStack result() {
            return small != null ? small : large;
        }

        /**
         * @return gets the 2x2 result, or null if none could be found
         */
        @Nullable
        public ItemStack getSmall() {
            return small;
        }

        /**
         * @return gets the 3x3 result, or null if none could be found
         */
        @Nullable
        public ItemStack getLarge() {
            return large;
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
        public @NotNull List<ItemStack> getItems() {
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
