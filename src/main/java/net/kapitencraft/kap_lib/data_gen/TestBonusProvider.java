package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.BonusProvider;
import net.kapitencraft.kap_lib.item.bonus.type.AttributeModifiersBonus;
import net.kapitencraft.kap_lib.item.bonus.type.EffectsBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestBonusProvider extends BonusProvider {

    public TestBonusProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, KapLibMod.MOD_ID, pLookupProvider, existingFileHelper);
    }

    @Override
    public void register() {
        this.createSetBonus("test")
                .slot(EquipmentSlot.HEAD, Items.DIAMOND_HELMET)
                .slot(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE)
                .slot(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS)
                .slot(EquipmentSlot.FEET, Items.DIAMOND_BOOTS)
                .setBonus(
                        new EffectsBonus(
                                List.of(
                                        new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1)
                                )
                        )
                );
        this.createSetBonus("attributes")
                .slot(EquipmentSlot.HEAD, Items.GOLDEN_HELMET)
                .slot(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE)
                .slot(EquipmentSlot.LEGS, Items.GOLDEN_LEGGINGS)
                .slot(EquipmentSlot.FEET, Items.GOLDEN_BOOTS)
                .slot(EquipmentSlot.MAINHAND, Items.GOLDEN_AXE)
                .setBonus(AttributeModifiersBonus.builder()
                        .addModifier(Attributes.LUCK, "TestBonusLuck", 20, AttributeModifier.Operation.ADDITION)
                        .build()
                );
        this.createItemBonus(Items.NETHERITE_SWORD, "sword_strength")
                .setBonus(new EffectsBonus(
                        List.of(
                                new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, 10)
                        )
                ));
    }
}
