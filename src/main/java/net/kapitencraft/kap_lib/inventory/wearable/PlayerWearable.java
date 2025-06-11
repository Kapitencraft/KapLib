package net.kapitencraft.kap_lib.inventory.wearable;

import net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@MethodsReturnNonnullByDefault
public class PlayerWearable implements Container {
    public static final Capability<PlayerWearable> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final WearableSlot[] SLOTS = createSlots();

    private static WearableSlot[] createSlots() {
        Collection<WearableSlot> collection = ExtraRegistries.WEARABLE_SLOTS.getValues();
        WearableSlot[] slots = new WearableSlot[collection.size()];
        for (WearableSlot slot : collection) {
            slots[slot.getSlotIndex()] = slot;
        }
        return slots;
    }

    private final NonNullList<ItemStack> content;
    private final Player player;

    public PlayerWearable(Player player) {
        this.player = player;
        this.content = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return SLOTS.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.content) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.content.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        if (pAmount > 0) {
            if (this.content.get(pSlot) != ItemStack.EMPTY) {
                ItemStack stack = this.content.get(pSlot);
                EquipmentPage.equip(this.player, SLOTS[pSlot], ItemStack.EMPTY, stack);
                this.content.set(pSlot, ItemStack.EMPTY);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.content.get(pSlot);
        this.content.set(pSlot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        this.content.set(pSlot, pStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {

    }

    public ListTag save() {
        ListTag tags = new ListTag();
        for (int i = 0; i < this.getContainerSize(); i++) {
            tags.add(this.getItem(i).save(new CompoundTag()));
        }
        return tags;
    }

    public void load(ListTag tags) {
        for (int i = 0; i < tags.size(); i++) {
            ItemStack stack = ItemStack.of(tags.getCompound(i));
            if (!stack.isEmpty()) EquipmentPage.equip(this.player, PlayerWearable.SLOTS[i], stack, ItemStack.EMPTY);
            this.setItem(i, stack);
        }
    }
}
