package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.minecraft.world.entity.LivingEntity;

public interface ExtendedAbilityEnchantment extends ModEnchantment {

    void onTick(LivingEntity source, int level);
}
