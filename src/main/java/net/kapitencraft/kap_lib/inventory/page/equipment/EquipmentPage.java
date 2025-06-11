package net.kapitencraft.kap_lib.inventory.page.equipment;

import net.kapitencraft.kap_lib.inventory.menu.SlotAdder;
import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.wearable.IWearable;
import net.kapitencraft.kap_lib.inventory.wearable.PlayerWearable;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaInventoryPages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * inventory page that contains all the wearable slots
 */
public class EquipmentPage extends InventoryPage {
    private static final ItemStack SYMBOL = new ItemStack(Items.DIAMOND_CHESTPLATE);

    public EquipmentPage(Player player, SlotAdder adder) {
        super(VanillaInventoryPages.EQUIPMENT.get());
        PlayerWearable wearable = player.getCapability(PlayerWearable.CAPABILITY).orElseThrow(() -> new IllegalStateException("unable to obtain player wearables!"));
        for (int i = 0; i < PlayerWearable.SLOTS.length; i++) {
            WearableSlot slot = PlayerWearable.SLOTS[i];
            adder.addSlot(new Slot(wearable, i, slot.getXPos(), slot.getYPos()) {
                @Override
                public void setByPlayer(@NotNull ItemStack pStack) {
                    equip(player, slot, pStack, this.getItem());
                    super.setByPlayer(pStack);
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack pStack) {
                    return pStack.is(slot.getTypeKey());
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
    }

    public static void equip(Player player, WearableSlot slot, ItemStack newItem, ItemStack oldItem) {
        if (!(oldItem.isEmpty() && newItem.isEmpty()) && !ItemStack.isSameItemSameTags(oldItem, newItem)) {
            if (newItem.getItem() instanceof IWearable wearable) {
                player.getAttributes().addTransientAttributeModifiers(wearable.getModifiers(slot, newItem));
            }
            if (oldItem.getItem() instanceof IWearable wearable) {
                player.getAttributes().removeAttributeModifiers(wearable.getModifiers(slot, oldItem));
            }
        }
    }

    @Override
    public @NotNull ItemStack symbol() {
        return SYMBOL;
    }

    @Override
    public boolean withInventory() {
        return true;
    }
}
