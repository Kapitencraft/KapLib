package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.BonusProvider;
import net.kapitencraft.kap_lib.item.bonus.type.EffectsBonus;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

import java.util.List;

public class ModBonusProvider extends BonusProvider {
    public ModBonusProvider(PackOutput output) {
        super(output, KapLibMod.MOD_ID);
    }

    @Override
    public void register() {
        this.createSetBonus("test")
                .slot(EquipmentSlot.HEAD, this.simpleItem(Items.DIAMOND_HELMET))
                .slot(EquipmentSlot.CHEST, this.simpleItem(Items.DIAMOND_CHESTPLATE))
                .slot(EquipmentSlot.LEGS, this.simpleItem(Items.DIAMOND_LEGGINGS))
                .slot(EquipmentSlot.FEET, this.simpleItem(Items.DIAMOND_BOOTS))
                .setBonus(
                        new EffectsBonus(
                                List.of(
                                        new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1)
                                )
                        )
                );
        this.createItemBonus(Items.NETHERITE_SWORD, "sword_strength")
                .setBonus(new EffectsBonus(
                        List.of(
                                new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, 10)
                        )
                ));
    }
}
