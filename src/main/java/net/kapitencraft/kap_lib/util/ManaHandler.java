package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.data_gen.ModDamageTypes;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber()
public class ManaHandler {

    public static final String OVERFLOW_MANA_ID = "overflowMana";

    @SuppressWarnings("all")
    @SubscribeEvent
    public static void manaChange(PlayerTickEvent event) {
        Player player = event.getEntity();
        AttributeInstance maxManaInstance = player.getAttribute(ExtraAttributes.MAX_MANA);
        if (!isMagical(player)) {
            throw new IllegalStateException("detected Player unable to use mana, expecting broken mod-state!");
        }
        double maxMana = maxManaInstance.getValue();
        double intel = player.getAttributeValue(ExtraAttributes.INTELLIGENCE);
        double curManaRegen = player.getAttributeValue(ExtraAttributes.MANA_REGEN);
        double manaRegen = maxMana / 500 * (1 + curManaRegen / 100);
        CompoundTag tag = player.getPersistentData();
        tag.putDouble("manaRegen", manaRegen);
        growMana(player, manaRegen);
        maxMana = 100 + intel;

        maxManaInstance.setBaseValue(maxMana);
    }

    public static boolean consumeMana(LivingEntity living, double manaToConsume) {
        if (!hasMana(living, manaToConsume)) return false;
        double mana = getMana(living);
        double overflow = getOverflow(living);
        if (overflow > manaToConsume) {
            overflow -= manaToConsume;
            manaToConsume = 0;
        } else {
            manaToConsume -= overflow;
            overflow = 0;
        }
        if (manaToConsume > 0) {
            mana -= manaToConsume;
        }
        setOverflow(living, overflow);
        setMana(living, mana);
        return true;
    }

    public static boolean hasMana(LivingEntity living, double manaToConsume) {
        if (manaToConsume <= 0) return true;
        if (!isMagical(living)) return false;
        double mana = getMana(living);
        double overflow = getOverflow(living);
        return mana + overflow >= manaToConsume;
    }

    public static double getOverflow(LivingEntity living) {
        return isMagical(living) ? living.getPersistentData().getDouble(OVERFLOW_MANA_ID) : 0;
    }

    public static double getMana(LivingEntity living) {
        return AttributeHelper.getSaveAttributeValue(ExtraAttributes.MANA, living);
    }

    public static void setMana(LivingEntity living, double mana) {
        AttributeInstance instance = living.getAttribute(ExtraAttributes.MANA);
        if (instance != null) {
            instance.setBaseValue(Math.min(mana, living.getAttributeValue(ExtraAttributes.MAX_MANA)));
        }
    }

    public static boolean growMana(LivingEntity living, double mana) {
        setMana(living, getMana(living) + mana);
        return isMagical(living);
    }

    public static boolean isMagical(LivingEntity living) {
        return living.getAttribute(ExtraAttributes.MANA) != null || living.getAttribute(ExtraAttributes.MAX_MANA) != null;
    }

    public static void setOverflow(LivingEntity living, double overflow) {
        if (overflow >= AttributeHelper.getSaveAttributeValue(ExtraAttributes.MAX_MANA, living)) {
            living.hurt(living.damageSources().source(ModDamageTypes.MANA_OVERFLOW_SELF), Float.MAX_VALUE);
            List<LivingEntity> livings = MathHelper.getLivingAround(living, 5);
            for (LivingEntity living1 : livings) {
                living1.hurt(living.damageSources().source(ModDamageTypes.MANA_OVERFLOW, living), (float) (Float.MAX_VALUE * (0.01 * Math.max(5 - living1.distanceTo(living), 0))));
            }
        }
        living.getPersistentData().putDouble(OVERFLOW_MANA_ID, overflow);
    }
}
