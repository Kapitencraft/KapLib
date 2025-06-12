package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * fired whenever a living entity's wearable slot changes detected in {@link net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage#equip(LivingEntity, WearableSlot, ItemStack, ItemStack) EquipmentPage#equip}
 * <br>this event is not cancellable and does not have a result
 */
public class WearableSlotChangeEvent extends LivingEvent {
    private final WearableSlot slot;
    private final ItemStack from, to;

    public WearableSlotChangeEvent(LivingEntity entity, WearableSlot slot, ItemStack from, ItemStack to) {
        super(entity);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public ItemStack getTo() {
        return to;
    }

    public WearableSlot getSlot() {
        return slot;
    }

    public ItemStack getFrom() {
        return from;
    }
}
