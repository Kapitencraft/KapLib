package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.data_gen.ModDamageTypes;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ModAttachmentTypes;
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
    public static void manaChange(PlayerTickEvent.Post event) {
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
        if (manaToConsume > 0) {
            mana -= manaToConsume;
        }
        setMana(living, mana);
        return true;
    }

    public static boolean hasMana(LivingEntity living, double manaToConsume) {
        if (manaToConsume <= 0) return true;
        if (!isMagical(living)) return false;
        return getMana(living) >= manaToConsume;
    }

    public static double getMana(LivingEntity living) {
        return living.getData(ModAttachmentTypes.MANA);
    }

    public static void setMana(LivingEntity living, double mana) {
        living.setData(ModAttachmentTypes.MANA, mana);
    }

    public static boolean growMana(LivingEntity living, double mana) {
        setMana(living, getMana(living) + mana);
        return isMagical(living);
    }

    public static boolean isMagical(LivingEntity living) {
        return living.getAttributes().hasAttribute(ExtraAttributes.MAX_MANA);
    }
}
