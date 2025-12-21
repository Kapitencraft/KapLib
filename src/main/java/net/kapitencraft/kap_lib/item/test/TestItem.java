package net.kapitencraft.kap_lib.item.test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableItem;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.kapitencraft.kap_lib.item.ExtendedItem;
import net.kapitencraft.kap_lib.registry.TestCooldowns;
import net.kapitencraft.kap_lib.inventory_page.registry.WearableSlots;
import net.kapitencraft.kap_lib.attribute.BaseAttributeLocations;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends WearableItem implements ExtendedItem {

    public TestItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(WearableSlot slot, ItemStack stack) {
        HashMultimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
        if (slot.is(WearableSlots.BELT)) {
            multimap.put(Attributes.LUCK, new AttributeModifier(BaseAttributeLocations.LUCK, 10, AttributeModifier.Operation.ADD_VALUE));
        }
        return multimap;
    }

    @Override
    public WearableSlot getSlot() {
        return WearableSlots.BELT.value();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            Vec3 angle = pPlayer.getLookAngle();
            pPlayer.setDeltaMovement(angle.scale(5));
            pPlayer.hurtMarked = true;
        }

        //if (TestCooldowns.TEST.value().isActive(pPlayer)) {
        //    pPlayer.sendSystemMessage(Component.literal("not work"));
        //} else {
        //    if (!pLevel.isClientSide()) TestCooldowns.TEST.value().applyCooldown(pPlayer, false);
        //    pPlayer.sendSystemMessage(Component.literal("started"));
        //    return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        //}
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    @Override
    public void appendHoverTextWithPlayer(@NotNull ItemStack itemStack, @Nullable TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag, Player player) {
        list.add(TestCooldowns.TEST.value().createDisplay(player));
    }
}
