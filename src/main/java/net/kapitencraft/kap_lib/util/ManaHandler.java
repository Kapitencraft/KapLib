package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.advancement.ExtraCriterionTriggers;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ModAttachmentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber()
public class ManaHandler {

    @SuppressWarnings("all")
    @SubscribeEvent
    public static void manaChange(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeInstance maxManaInstance = player.getAttribute(ExtraAttributes.MAX_MANA);
        if (!isMagical(player)) {
            throw new IllegalStateException("detected Player unable to use mana, expecting broken mod-state!");
        }
        double manaRegen = player.getAttributeValue(ExtraAttributes.MANA_REGEN);
        CompoundTag tag = player.getPersistentData();
        setMana(player, Math.min(getMana(player) + manaRegen, player.getAttributeValue(ExtraAttributes.MAX_MANA)));
    }

    /**
     * @param living the entity to try and consume the mana of
     * @param manaToConsume the amount of mana to consume
     * @return whether the consumption was successful
     */
    public static boolean consumeMana(LivingEntity living, double manaToConsume) {
        if (!hasMana(living, manaToConsume)) return false;
        double mana = getMana(living);
        if (manaToConsume > 0) {
            mana -= manaToConsume;
            if (living instanceof ServerPlayer serverPlayer) {
                ExtraCriterionTriggers.MANA_CONSUMED.get().trigger(serverPlayer, manaToConsume);
            }
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

    public static boolean isMagical(LivingEntity living) {
        return living.getAttributes().hasAttribute(ExtraAttributes.MAX_MANA);
    }
}
