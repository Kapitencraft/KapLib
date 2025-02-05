package net.kapitencraft.kap_lib.helpers;

import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.enchantments.abstracts.ExtendedAbilityEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@Mod.EventBusSubscriber
public interface BonusHelper {


    /**
     * @param living the target entity to tick enchantments of
     * ticks any {@link ExtendedAbilityEnchantment} applied to any of the 6 {@link EquipmentSlot}s available
     */
    static void tickEnchantments(LivingEntity living) {
        doForSlot((stack, slot) -> MapStream.of(stack.getAllEnchantments())
                        .filterKeys(ExtendedAbilityEnchantment.class::isInstance)
                        .mapKeys(ExtendedAbilityEnchantment.class::cast)
                        .forEach((enchantment, integer) -> enchantment.onTick(living, integer)),
                living,
                (stack, slot) -> stack.isEnchanted() && LivingEntity.getEquipmentSlotForItem(stack) == slot
        );
    }


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
