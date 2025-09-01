package net.kapitencraft.kap_lib.helpers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface BonusHelper {

    /**
     * do something with each of the 6 {@link EquipmentSlot}s available on the {@link LivingEntity} given
     * @param stackConsumer what to do with the stack and the slot
     * @param living the entity to do it on
     * @param usagePredicate whether to execute it on the slot, or not
     */
    static void doForSlot(BiConsumer<ItemStack, EquipmentSlot> stackConsumer, LivingEntity living, BiPredicate<ItemStack, EquipmentSlot> usagePredicate) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = living.getItemBySlot(slot);
            if (usagePredicate.test(stack, slot)) stackConsumer.accept(stack, slot);
        }
    }
}
