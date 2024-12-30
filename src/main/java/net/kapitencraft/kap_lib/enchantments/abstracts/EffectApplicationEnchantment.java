package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public interface EffectApplicationEnchantment extends ExtendedCalculationEnchantment {

    @Override
    default @NotNull ProcessPriority priority() {
        return ProcessPriority.LOWEST;
    }

    MobEffect getEffect();

    int getChance(int level);

    int getScale();

    @Override
    default double execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damage, DamageSource source) {
        if (!MiscHelper.increaseEffectDuration(attacked, getEffect(), level * getScale()) && MathHelper.chance(getChance(level) / 100., attacker)) {
            attacked.addEffect(new MobEffectInstance(getEffect(), level * getScale(), 1));
        }
        return damage;
    }

    @Override
    default String[] getDescriptionMods(int level) {
        return new String[] {getChance(level) + "%", "" + level*getScale()};
    }

}