package net.kapitencraft.kap_lib.item.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.NotNull;

public abstract class LibSwordItem extends SwordItem {

    public LibSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public LibSwordItem(Tier p_tier, Properties p_properties, Tool toolComponentData) {
        super(p_tier, p_properties, toolComponentData);
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
