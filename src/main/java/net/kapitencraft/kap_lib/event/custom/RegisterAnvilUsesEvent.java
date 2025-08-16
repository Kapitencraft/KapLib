package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.item.misc.AnvilUses;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * event to register custom anvil recipes to the handler <br>
 * use {@link net.kapitencraft.kap_lib.item.misc.AnvilUses#registerAnvilUse(BiPredicate, BiConsumer, int) AnvilUses#registerAnvilUse()} to register new ones
 */
public class RegisterAnvilUsesEvent extends Event implements IModBusEvent {

    /**
     * @param bothPredicate predicate for both anvil inputs
     * @param resultConsumer results. modify the left stack
     * @param xpCost the amount of XP this anvil use should take
     */
    public void registerAnvilUse(BiPredicate<ItemStack, ItemStack> bothPredicate, BiConsumer<ItemStack, ItemStack> resultConsumer, int xpCost) {
        AnvilUses.registerAnvilUse(bothPredicate, resultConsumer, xpCost);
    }
}
