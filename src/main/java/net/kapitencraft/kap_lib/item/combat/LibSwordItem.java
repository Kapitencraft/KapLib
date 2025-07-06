package net.kapitencraft.kap_lib.item.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public abstract class LibSwordItem extends SwordItem {
    public LibSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    /**
     * @return the {@link ResourceKey} to the damage type this sword should use when attacking entities
     * defaults to {@link net.minecraft.world.damagesource.DamageTypes#PLAYER_ATTACK DamageTypes#PLAYER_ATTACK}
     *
     */
    public @NotNull ResourceKey<DamageType> getDamageType() {
        return DamageTypes.PLAYER_ATTACK;
    }
}
