package net.kapitencraft.kap_lib.mana;

import net.kapitencraft.kap_lib.mana.advancement.ExtraCriterionTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * handles anything mana related, such as {@link #setMana(LivingEntity, double) setting}, {@link #getMana(LivingEntity) getting} or {@link #consumeMana(LivingEntity, double) consuming} mana
 */
@EventBusSubscriber(modid = ManaModule.MODULE_ID)
public class ManaHandler {

    @SuppressWarnings("all")
    @SubscribeEvent
    @ApiStatus.Internal
    public static void manaChange(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeInstance maxManaInstance = player.getAttribute(ManaAttributes.MAX_MANA);
        if (!isMagical(player)) {
            throw new IllegalStateException("detected Player unable to use mana, expecting broken mod-state!");
        }
        double manaRegen = player.getAttributeValue(ManaAttributes.MANA_REGEN);
        CompoundTag tag = player.getPersistentData();
        setMana(player, Math.min(getMana(player) + manaRegen, player.getAttributeValue(ManaAttributes.MAX_MANA)));
    }

    /**
     * consumes the given amount of mana on the given entity
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

    /**
     * checks if the given entity has enough mana
     * @param living the given entity
     * @param manaToConsume the amount of mana required
     * @return whether
     */
    public static boolean hasMana(LivingEntity living, double manaToConsume) {
        if (manaToConsume <= 0) return true;
        if (!isMagical(living)) return false;
        return getMana(living) >= manaToConsume;
    }

    /**
     * gets the mana of the entity
     * @param living the entity to get the mana of
     * @return the amount of mana the given entity has
     */
    public static double getMana(LivingEntity living) {
        return living.getData(ManaAttachmentTypes.MANA);
    }

    /**
     * @param living the entity of which to set the mana
     * @param mana the amount of mana to set
     */
    public static void setMana(LivingEntity living, double mana) {
        living.setData(ManaAttachmentTypes.MANA, mana);
    }

    /**
     * checks if the given entity is magical
     * @param living the entity to check if it's magical
     * @return whether the entity is magical, that is, has the MAX_MANA attribute
     */
    public static boolean isMagical(LivingEntity living) {
        return living.getAttributes().hasAttribute(ManaAttributes.MAX_MANA);
    }
}
