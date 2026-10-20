package net.kapitencraft.kap_lib.item.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * API for simple item compacting
 */
public class Compacting {
    private static final Map<Item, Result> resultCache = new HashMap<>();

    public static Result tryCompact(Item in, ServerLevel level) {
        if (resultCache.containsKey(in)) return resultCache.get(in);
        RecipeManager manager = level.getRecipeManager();

        Optional<RecipeHolder<CraftingRecipe>> smallRecipe = manager.getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(2, 2, list(true, in)), level);
        Optional<RecipeHolder<CraftingRecipe>> largeRecipe = manager.getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(3, 3, list(false, in)), level);

        Result result;
        if (smallRecipe.isEmpty() && largeRecipe.isEmpty())
            result = Result.EMPTY;
        else
            result = new Result(
                smallRecipe.map(craftingRecipe -> craftingRecipe.value().getResultItem(level.registryAccess())).orElse(null),
                largeRecipe.map(craftingRecipe -> craftingRecipe.value().getResultItem(level.registryAccess())).orElse(null)
            );
        resultCache.put(in, result);

        return result;
    }

    private static List<ItemStack> list(boolean small, Item in) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0;  i < (small ? 4 : 9); i++) {
            list.add(new ItemStack(in));
        }
        return list;
    }

    public static class Result {
        public static final Result EMPTY = new Result(null, null);

        private final @Nullable ItemStack small, large;

        private Result(@Nullable ItemStack small, @Nullable ItemStack large) {
            this.small = small;
            this.large = large;
        }

        public boolean successful() {
            return small != null || large != null;
        }

        public boolean isSmall() {
            return successful() && small != null;
        }

        public int getCountReq() {
            return small != null ? 4 : large != null ? 9 : -1;
        }

        public @Nullable ItemStack result() {
            return small != null ? small : large;
        }

        @Nullable
        public ItemStack getSmall() {
            return small;
        }

        @Nullable
        public ItemStack getLarge() {
            return large;
        }
    }
}
