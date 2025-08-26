package net.kapitencraft.kap_lib.helpers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface InventoryHelper {

    /**
     * check if the player has an ItemStack with that item in the inventory
     */
    static boolean hasPlayerStackInInventory(Player player, Item item) {
        Inventory inventory = player.getInventory();
        return allInventory(inventory).stream()
                .anyMatch(stack -> !stack.isEmpty() && stack.is(item));
    }

    /**
     * do something with each of the inventories slots
     */
    static void forInventory(Inventory inventory, Consumer<ItemStack> consumer) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            consumer.accept(inventory.getItem(i));
        }
    }

    static Map<EquipmentSlot, ItemStack> equipment(LivingEntity living) {
        return Arrays.stream(EquipmentSlot.values()).collect(CollectorHelper.createMap(living::getItemBySlot));
    }

    /**
     * gets a map of all {@link ItemStack} inside the inventory of a player mapped to their slot id
     */
    static Map<Integer, ItemStack> getAllContent(Inventory inventory) {
        Map<Integer, ItemStack> data = new HashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            data.put(i, inventory.getItem(i));
        }
        return data;
    }

    /**
     * gets a list of all ItemStacks in the inventory
     */
    static List<ItemStack> allInventory(Inventory inventory) {
        List<ItemStack> list = new ArrayList<>();
        forInventory(inventory, list::add);
        return list;
    }

    /**
     * removes the given stacks size amount of items, matching the stack from the player's inventory
     * @return if all items have been removed
     */
    static boolean removeFromInventory(ItemStack stack, Player player) {
        Inventory inventory = player.getInventory();
        forInventory(inventory, stack1 -> {
            if (ItemStack.isSameItemSameTags(stack, stack1) && stack.getCount() > 0) {
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
    static int getFirstInventoryIndex(Item item, Player player) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * @return the ItemStack inside the first slot matching the given item in the given player's inventory
     */
    static ItemStack getFirstStackInventoryIndex(Player player, Item item) {
        return allInventory(player.getInventory()).stream()
                .filter(byItem(item))
                .findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * get all ItemStacks from the player's inventory that match the given predicate
     */
    static Collection<ItemStack> getByFilter(Player player, Predicate<ItemStack> predicate) {
        return getContentByFilter(player, predicate).values();
    }

    /**
     * get all ItemStacks mapped with their slots from the player's inventory that match the given predicate
     */
    static Map<Integer, ItemStack> getContentByFilter(Player player, Predicate<ItemStack> predicate) {
        Map<Integer, ItemStack> itemStacks = new HashMap<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (predicate.test(stack)) itemStacks.put(i, stack);
        }
        return itemStacks;
    }

    /**
     * create a predicate that checks any ItemStack for the given item
     */
    private static Predicate<ItemStack> byItem(Item item) {
        return stack -> stack.is(item);
    }

    /**
     * check if the player has a full set (helmet, chestplate, leggings, boots) if the given {@code material}
     * equipped
     */
    static boolean hasSetInInventory(Player player, ArmorMaterial material) {
        List<EquipmentSlot> slots = new ArrayList<>();
        allInventory(player.getInventory()).stream().map(ItemStack::getItem).filter(
                item -> item instanceof ArmorItem armorItem && armorItem.getMaterial() == material
        ).map(ArmorItem.class::cast).map(ArmorItem::getEquipmentSlot).forEach(slots::add);
        return !slots.isEmpty() && new HashSet<>(slots).containsAll(Arrays.stream(MiscHelper.ARMOR_EQUIPMENT).toList());
    }

    /**
     * @return if all ItemStacks can be found in the player's inventory
     */
    static boolean hasInInventory(List<ItemStack> content, Player player) {
        return getRemaining(content, player).isEmpty();
    }

    /**
     * @return all ItemStacks that could not be found in the inventory checking for the given content
     */
    static List<ItemStack> getRemaining(List<ItemStack> content, Player player) {
        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack stack : content) {
            Collection<ItemStack> list = getByFilter(player, stack1 -> ItemStack.isSameItemSameTags(stack, stack1));
            list.forEach(stack1 -> stack.shrink(stack1.getCount()));
            if (stack.getCount() > 0) {
                ret.add(stack);
            }
        }
        return ret;
    }
}