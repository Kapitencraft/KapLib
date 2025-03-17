package net.kapitencraft.kap_lib.item.combat.totem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * base class for totems.
 * overwrite {@link #onUse(LivingEntity, DamageSource)} to enable behaviour
 */
public abstract class ModTotemItem extends Item {
    public ModTotemItem(Properties p_41383_) {
        super(p_41383_.stacksTo(1));
    }

    /**
     * @param living the entity dying
     * @param source the source that caused the death
     * @return whether this totem saves the entity
     * @implNote make sure to set the health of the entity above 0 when you save it, as otherwise the game will crash
     */
    public abstract boolean onUse(LivingEntity living, DamageSource source);
}
