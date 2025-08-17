package net.kapitencraft.kap_lib.inventory.page.equipment;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.event.custom.WearableSlotChangeEvent;
import net.kapitencraft.kap_lib.inventory.menu.SlotAdder;
import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.wearable.IWearable;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaInventoryPages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * inventory page that contains all the wearable slots
 */
public class EquipmentPage extends InventoryPage {
    private static final ItemStack SYMBOL = new ItemStack(Items.DIAMOND_CHESTPLATE);

    public EquipmentPage(Player player, SlotAdder adder) {
        super(VanillaInventoryPages.EQUIPMENT.get());
        Wearables wearable = Objects.requireNonNull(player.getCapability(Wearables.CAPABILITY), "unable to obtain player wearables!");
        for (int i = 0; i < Wearables.SLOTS.length; i++) {
            WearableSlot slot = Wearables.SLOTS[i];
            adder.addSlot(new Slot(wearable, i, slot.getXPos(), slot.getYPos()) {
                @Override
                public void setByPlayer(@NotNull ItemStack pStack) {
                    equip(player, slot, pStack, this.getItem());
                    super.setByPlayer(pStack);
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack pStack) {
                    return pStack.getItem() instanceof IWearable wearable && wearable.getSlot() == slot;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return slot.getNoItemIcon();
                }
            });
        }
    }

    public static void equip(LivingEntity living, WearableSlot slot, ItemStack newItem, ItemStack oldItem) {
        if (!(oldItem.isEmpty() && newItem.isEmpty()) && !ItemStack.isSameItemSameComponents(oldItem, newItem)) {
            if (newItem.getItem() instanceof IWearable wearable) {
                living.getAttributes().addTransientAttributeModifiers(wearable.getModifiers(slot, newItem));
            }
            if (oldItem.getItem() instanceof IWearable wearable) {
                living.getAttributes().removeAttributeModifiers(wearable.getModifiers(slot, oldItem));
            }
            NeoForge.EVENT_BUS.post(new WearableSlotChangeEvent(living, slot, oldItem, newItem));
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
