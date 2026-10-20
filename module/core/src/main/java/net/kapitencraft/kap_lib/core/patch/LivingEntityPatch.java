package net.kapitencraft.kap_lib.core.patch;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LivingEntityPatch {

    /**
     * swaps the items in the two hands of the given entity
     */
    default void swapHands() {
        LivingEntity living = (LivingEntity) this;
        ItemStack mainHand = living.getMainHandItem();
        living.setItemInHand(InteractionHand.MAIN_HAND, living.getOffhandItem());
        living.setItemInHand(InteractionHand.OFF_HAND, mainHand);
    }

    /**
     * only increases, not adds the effect duration
     *
     * @param effect the effect to increase
     * @param ticks  the amount of time, in ticks, to increase by
     * @return whether the effect was active and has been increased
     */
    default boolean increaseEffectDuration(Holder<MobEffect> effect, int ticks) {
        LivingEntity living = (LivingEntity) this;
        if (living.hasEffect(effect)) {
            MobEffectInstance oldInstance = living.getEffect(effect);
            assert oldInstance != null;
            oldInstance.duration += ticks;
            return true;
        }
        return false;
    }

    /**
     * sets the effect duration of the given effect to at least minTicks
     * @param effect the effect to set the duration for
     * @param minTicks the minimum remaining tick count to be set for the effect
     */
    default void maxEffectDuration(Holder<MobEffect> effect, int minTicks) {
        LivingEntity living = (LivingEntity) this;
        if (living.hasEffect(effect)) {
            MobEffectInstance oldInstance = living.getEffect(effect);
            assert oldInstance != null;
            oldInstance.duration = Math.max(oldInstance.duration, minTicks);
        } else {
            living.addEffect(new MobEffectInstance(effect, minTicks));
        }
    }
}
