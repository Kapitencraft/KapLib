package net.kapitencraft.kap_lib.item.misc;

import net.kapitencraft.kap_lib.event.ModEventFactory;
import net.kapitencraft.kap_lib.event.custom.RegisterAnvilUsesEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * manager for anvil recipes.
 * register new ones using {@link RegisterAnvilUsesEvent}
 */
@EventBusSubscriber
public class AnvilUses {
    private static final List<AnvilUse> uses = new ArrayList<>();

    @SubscribeEvent
    public static void anvilEvent(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        for (AnvilUse use : uses) {
            if (use.bothPredicate.test(left, right)) {
                ItemStack output = left.copy();
                use.resultConsumer.accept(output, right);
                event.setOutput(output);
                event.setCost(use.xpCost);
            }
        }
    }

    /**
     * @param bothPredicate predicate for both anvil inputs
     * @param resultConsumer results. modify the left stack
     * @param xpCost the amount of XP this anvil use should take
     * @deprecated use {@link RegisterAnvilUsesEvent#registerAnvilUse(BiPredicate, BiConsumer, int)} instead
     */
    @Deprecated
    @ApiStatus.Internal
    public static void registerAnvilUse(BiPredicate<ItemStack, ItemStack> bothPredicate, BiConsumer<ItemStack, ItemStack> resultConsumer, int xpCost) {
        uses.add(new AnvilUse(bothPredicate, resultConsumer, xpCost));
    }

    @ApiStatus.Internal
    public static void registerUses() {
        ModEventFactory.fireModEvent(new RegisterAnvilUsesEvent());
    }

    @ApiStatus.Internal
    private record AnvilUse(BiPredicate<ItemStack, ItemStack> bothPredicate,
                            BiConsumer<ItemStack, ItemStack> resultConsumer, int xpCost) {
    }

    private static BiPredicate<ItemStack, ItemStack> simple(Predicate<ItemStack> both) {
        return (stack, stack2) -> both.test(stack) && both.test(stack2);
    }

    private static BiPredicate<ItemStack, ItemStack> both(Predicate<ItemStack> first, Predicate<ItemStack> second) {
        return (stack, stack2) -> first.test(stack) && second.test(stack2);
    }
}
