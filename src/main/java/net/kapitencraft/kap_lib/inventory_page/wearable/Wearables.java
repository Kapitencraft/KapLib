package net.kapitencraft.kap_lib.inventory_page.wearable;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.inventory_page.page.equipment.EquipmentPage;
import net.kapitencraft.kap_lib.inventory_page.registry.WearableAttachmentTypes;
import net.kapitencraft.kap_lib.inventory_page.registry.custom.InventoryPageRegistries;
import net.kapitencraft.kap_lib.inventory_page.network.S2C.SyncWearablesToPlayerPacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
public class Wearables implements Container {

    public static final WearableSlot[] SLOTS = createSlots();

    private static WearableSlot[] createSlots() {
        Collection<WearableSlot> collection = (InventoryPageRegistries.WEARABLE_SLOTS).stream().toList();
        WearableSlot[] slots = new WearableSlot[collection.size()];
        for (WearableSlot slot : collection) {
            slots[slot.getSlotIndex()] = slot;
        }
        return slots;
    }

    private final NonNullList<ItemStack> content;
    private LivingEntity owner;

    public static final Codec<Wearables> CODEC = NonNullList.codecOf(ItemStack.OPTIONAL_CODEC).xmap(Wearables::new, w -> w.content);

    public Wearables() {
        this.content = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    private Wearables(NonNullList<ItemStack> list) {
        this.content = list;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public static Wearables get(@NotNull LivingEntity living) {
        Wearables data = Objects.requireNonNull(living.getData(WearableAttachmentTypes.WEARABLES.get()), "capability not found!");
        data.setOwner(living);
        return data;
    }

    public static void send(ServerPlayer sP) {
        Wearables wearables = get(sP);
        PacketDistributor.sendToPlayer(sP, new SyncWearablesToPlayerPacket(sP.getId(), wearables.content));
    }

    public int getContainerSize() {
        return SLOTS.length;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.content) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public ItemStack getItem(int pSlot) {
        return this.content.get(pSlot);
    }

    public ItemStack get(WearableSlot slot) {
        return getItem(slot.getSlotIndex());
    }

    public ItemStack removeItem(int pSlot, int pAmount) {
        if (pAmount > 0) {
            if (this.content.get(pSlot) != ItemStack.EMPTY) {
                ItemStack stack = this.content.get(pSlot);
                this.setItem(pSlot, ItemStack.EMPTY);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.content.get(pSlot);
        this.content.set(pSlot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        EquipmentPage.equip(owner, SLOTS[pSlot], pStack, this.content.get(pSlot));
        this.content.set(pSlot, pStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return pPlayer == owner;
    }

    public void clearContent() {

    }

    public void copyFrom(List<ItemStack> content) {
        for (int i = 0; i < content.size(); i++) {
            this.setItem(i, content.get(i));
        }
    }
}