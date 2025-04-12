package net.kapitencraft.kap_lib.item.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public abstract class LibSwordItem extends SwordItem {
    public LibSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    public abstract ResourceKey<DamageType> getType();
}
