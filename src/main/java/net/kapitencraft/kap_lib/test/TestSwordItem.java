package net.kapitencraft.kap_lib.test;

import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Tiers;

public class TestSwordItem extends LibSwordItem {
    public TestSwordItem() {
        super(Tiers.DIAMOND, 10, -2.2f, new Properties());
    }

    @Override
    public ResourceKey<DamageType> getType() {
        return DamageTypes.FIREBALL;
    }
}
