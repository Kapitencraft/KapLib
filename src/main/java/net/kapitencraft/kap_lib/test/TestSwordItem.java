package net.kapitencraft.kap_lib.test;

import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

public class TestSwordItem extends LibSwordItem {
    public TestSwordItem() {
        super(Tiers.DIAMOND, new Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, 10, -2.2f)));
    }

    @Override
    public @NotNull ResourceKey<DamageType> getDamageType() {
        return DamageTypes.FIREBALL;
    }
}
