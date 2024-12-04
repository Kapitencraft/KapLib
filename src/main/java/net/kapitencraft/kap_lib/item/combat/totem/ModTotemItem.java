package net.kapitencraft.kap_lib.item.combat.totem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public abstract class ModTotemItem extends Item {
    public ModTotemItem(Properties p_41383_) {
        super(p_41383_.stacksTo(1));
    }

    public abstract boolean onUse(LivingEntity living, DamageSource source);
}
