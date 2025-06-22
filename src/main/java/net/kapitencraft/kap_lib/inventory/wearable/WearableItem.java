package net.kapitencraft.kap_lib.inventory.wearable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * simple implementation for wearable items
 */
public abstract class WearableItem extends Item implements IWearable {
    public WearableItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return IWearable.super.swapWithEquipmentSlot(this, pLevel, pPlayer, pUsedHand);
    }
}
