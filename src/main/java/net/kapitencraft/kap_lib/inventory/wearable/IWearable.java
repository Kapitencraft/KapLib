package net.kapitencraft.kap_lib.inventory.wearable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * item extension for wearables (see {@link net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPage EquipmentPage} for more info)
 */
public interface IWearable {

    default Multimap<Attribute, AttributeModifier> getModifiers(WearableSlot slot, ItemStack stack) {
        return ImmutableMultimap.of();
    }

    WearableSlot getSlot();

    default InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item pItem, Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        WearableSlot slot = Objects.requireNonNull(pItem instanceof IWearable wearable ? wearable.getSlot() : null, "item not wearable: " + ForgeRegistries.ITEMS.getKey(pItem));
        Wearables wearables = Wearables.get(pPlayer);
        ItemStack original = wearables.get(slot);
        if (!EnchantmentHelper.hasBindingCurse(original) && !ItemStack.matches(itemstack, original)) {
            if (!pLevel.isClientSide()) {
                pPlayer.awardStat(Stats.ITEM_USED.get(pItem));
            }

            ItemStack stack = original.isEmpty() ? itemstack : original.copyAndClear();
            ItemStack stack1 = itemstack.copyAndClear();
            wearables.setItem(slot.getSlotIndex(), stack1);
            return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }
}
