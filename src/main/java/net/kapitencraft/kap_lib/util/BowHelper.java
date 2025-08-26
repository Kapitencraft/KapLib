package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class BowHelper {

    private static final float OFFSET_DEGREES = 4f;

    public static AbstractArrow createArrowProperties(LivingEntity archer, boolean crit, ItemStack bow, int kb, float rotX, float rotY) {
        Level world = archer.level();
        ItemStack arrowStack = archer.getProjectile(bow);
        if (!arrowStack.isEmpty() && arrowStack.getItem() instanceof ArrowItem arrowItem) {
            AbstractArrow arrow = arrowItem.createArrow(world, arrowStack, archer);
            arrow.shootFromRotation(archer, rotX, rotY, 0.0F, 5, 1.0F);
            arrow.setBaseDamage(archer.getAttributeValue(ExtraAttributes.RANGED_DAMAGE.get()));
            arrow.setKnockback(kb);
            arrow.setCritArrow(crit);
            registerEnchant(bow, arrow);
            if (archer instanceof Player player && isInfinite(player, arrowStack, bow)) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            bow.hurtAndBreak(1, archer, (living) -> living.broadcastBreakEvent(archer.getUsedItemHand()));
            world.addFreshEntity(arrow);
            if ( !(archer instanceof Player player && player.getAbilities().instabuild) || bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0) {
                arrowStack.shrink(1);
                if (arrowStack.isEmpty() && archer instanceof Player player) player.getInventory().removeItem(arrowStack);
            }
            world.playSound(null, archer.getX(), archer.getY(), archer.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            return arrow;
        } else { return null;}
    }


    /**
     * adds enchantment value modifiers to the given arrow
     */
    protected static void registerEnchant(ItemStack bow, AbstractArrow arrow) {
        int j = bow.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (j > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double)j * 0.5D + 0.5D);
        }

        int k = bow.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
        if (k > 0) {
            arrow.setKnockback(arrow.getKnockback() + k);
        }

        if (bow.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
            arrow.setSecondsOnFire(100);
        }
    }

    private static boolean isInfinite(Player player, ItemStack itemStack, ItemStack bow) {
        boolean flag1 = player.getAbilities().instabuild || (itemStack.getItem() instanceof ArrowItem && ((ArrowItem)itemStack.getItem()).isInfinite(itemStack, bow, player));
        return flag1 || player.getAbilities().instabuild && (itemStack.is(Items.SPECTRAL_ARROW) || itemStack.is(Items.TIPPED_ARROW));
    }
}
