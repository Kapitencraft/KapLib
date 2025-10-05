package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.data_gen.ModDamageTypes;
import net.kapitencraft.kap_lib.event.custom.PlayerChangeManaEvent;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@Mod.EventBusSubscriber()
public class ManaHandler {

    @SubscribeEvent
    @ApiStatus.Internal
    public static void manaChange(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!isMagical(player)) {
            throw new IllegalStateException("detected Player unable to use mana, expecting broken mod-state!");
        }
        growMana(player, player.getAttributeValue(ExtraAttributes.MANA_REGEN.get()));
    }

    /**
     * @param living the entity that consumed the mana
     * @param manaToConsume the amount of mana to consume
     * @return whether the consumption was successful
     */
    public static boolean consumeMana(LivingEntity living, double manaToConsume) {
        if (!hasMana(living, manaToConsume)) return false;
        double mana = getMana(living);

        if (manaToConsume > 0) {
            mana -= manaToConsume;
        }
        setMana(living, mana, PlayerChangeManaEvent.Reason.USAGE);
        return true;
    }

    public static boolean hasMana(LivingEntity living, double manaToConsume) {
        if (manaToConsume <= 0) return true;
        if (!isMagical(living)) return false;
        double mana = getMana(living);
        return mana >= manaToConsume;
    }

    /**
     * @param living the entity to get the mana of
     * @return the mana of the entity
     */
    public static double getMana(LivingEntity living) {
        return AttributeHelper.getSaveAttributeValue(ExtraAttributes.MANA.get(), living);
    }

    /**
     * @param living the entity to set the mana of
     * @param mana the amount of mana to set
     * @param reason the reason for the change
     */
    public static void setMana(LivingEntity living, double mana, PlayerChangeManaEvent.Reason reason) {
        AttributeInstance instance = living.getAttribute(ExtraAttributes.MANA.get());
        if (instance != null) {
            double manaValue = Math.min(mana, living.getAttributeValue(ExtraAttributes.MAX_MANA.get()));
            if (living instanceof Player player) {
                PlayerChangeManaEvent event = new PlayerChangeManaEvent(player, manaValue, reason);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) return;
                manaValue = event.getNewMana();
            }
            instance.setBaseValue(manaValue);
        }
    }

    private static void growMana(LivingEntity living, double mana) {
        setMana(living, getMana(living) + mana, PlayerChangeManaEvent.Reason.NATURAL);
    }

    /**
     * @param living the entity to check
     * @return whether this entity is able to use magic (e.g. has the required attributes)
     */
    public static boolean isMagical(LivingEntity living) {
        return living.getAttribute(ExtraAttributes.MANA.get()) != null || living.getAttribute(ExtraAttributes.MAX_MANA.get()) != null;
    }
}
