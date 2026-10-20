package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;

public interface PlayerPatch {

    /**
     * check if the player has an ItemStack with that item in the inventory
     */
    default boolean hasPlayerStackInInventory(Item item) {
        Inventory inventory = ((Player) this).getInventory();
        return InventoryHelper.allInventory(inventory).stream()
                .anyMatch(stack -> !stack.isEmpty() && stack.is(item));
    }

    /**
     * removes the given stacks size amount of items, matching the stack from the player's inventory
     * @return if all items have been removed
     */
    default boolean removeFromInventory(ItemStack stack) {
        Inventory inventory = ((Player) this).getInventory();
        InventoryHelper.forInventory(inventory, stack1 -> {
            if (ItemStack.isSameItemSameComponents(stack, stack1) && stack.getCount() > 0) {
                int size = Math.min(stack1.getCount(), stack.getCount());
                stack1.shrink(size);
                stack.shrink(size);
            }
        });
        return stack.getCount() <= 0;
    }

    /**
     * @return the first slot index of an ItemStack matching the given Item, or -1 if none could be found
     */
    default int getFirstInventoryIndex(Item item) {
        Inventory inventory = ((Player) this).getInventory();
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack currentStack = inventory.getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return the ItemStack inside the first slot matching the given item in the given player's inventory
     */
    default ItemStack getFirstStackInventoryIndex(Item item) {
        return InventoryHelper.allInventory(((Player) this).getInventory()).stream()
                .filter(stack -> stack.is(item))
                .findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * get all ItemStacks from the player's inventory that match the given predicate
     */
    default Collection<ItemStack> getInventoryItemsByFilter(Predicate<ItemStack> predicate) {
        return getInventoryContentByFilter(predicate).values();
    }

    /**
     * get all ItemStacks mapped with their slots from the player's inventory that match the given predicate
     */
    default Map<Integer, ItemStack> getInventoryContentByFilter(Predicate<ItemStack> predicate) {
        Inventory inventory = ((Player) this).getInventory();
        Map<Integer, ItemStack> itemStacks = new HashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (predicate.test(stack)) itemStacks.put(i, stack);
        }
        return itemStacks;
    }

    /**
     * check if the player has a full set (helmet, chestplate, leggings, boots) if the given {@code material}
     * equipped
     */
    default boolean hasSetInInventory(Holder<ArmorMaterial> material) {
        List<EquipmentSlot> slots = new ArrayList<>();
        InventoryHelper.allInventory(((Player) this).getInventory()).stream().map(ItemStack::getItem).filter(
                item -> item instanceof ArmorItem armorItem && armorItem.getMaterial() == material
        ).map(ArmorItem.class::cast).map(ArmorItem::getEquipmentSlot).forEach(slots::add);
        return !slots.isEmpty() && new HashSet<>(slots).containsAll(Arrays.stream(MiscHelper.ARMOR_EQUIPMENT).toList());
    }

    /**
     * @return if all ItemStacks can be found in the player's inventory
     */
    default boolean hasInInventory(List<ItemStack> content) {
        return getRemainingFromInventory(content).isEmpty();
    }

    /**
     * @return all ItemStacks that could not be found in the inventory checking for the given content
     */
    default List<ItemStack> getRemainingFromInventory(List<ItemStack> content) {
        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack stack : content) {
            Collection<ItemStack> list = getInventoryItemsByFilter(stack1 -> ItemStack.isSameItemSameComponents(stack, stack1));
            list.forEach(stack1 -> stack.shrink(stack1.getCount()));
            if (stack.getCount() > 0) {
                ret.add(stack);
            }
        }
        return ret;
    }
}
