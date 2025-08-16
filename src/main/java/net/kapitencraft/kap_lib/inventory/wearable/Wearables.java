package net.kapitencraft.kap_lib.inventory.wearable;

import net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.capability.SyncWearablesToPlayerPacket;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
public class Wearables implements Container {

    public static final Capability<Wearables> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

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
    private final LivingEntity entity;

    public Wearables(LivingEntity entity) {
        this.entity = entity;
        this.content = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    public static Wearables get(@NotNull LivingEntity living) {
        return living.getCapability(CAPABILITY).orElseThrow(() -> new IllegalArgumentException("capability not found!"));
    }

    public static void send(ServerPlayer sP) {
        Wearables wearables = get(sP);
        PacketDistributor.sendToPlayer(sP, new SyncWearablesToPlayerPacket(sP.getId(), wearables.content));
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

    public ItemStack get(WearableSlot slot) {
        return getItem(slot.getSlotIndex());
    }

    @Override
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

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.content.get(pSlot);
        this.content.set(pSlot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        EquipmentPage.equip(this.entity, SLOTS[pSlot], pStack, this.content.get(pSlot));
        this.content.set(pSlot, pStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return pPlayer == this.entity;
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
            this.setItem(i, stack);
        }
    }

    public void copyFrom(List<ItemStack> content) {
        for (int i = 0; i < content.size(); i++) {
            this.setItem(i, content.get(i));
        }
    }
}