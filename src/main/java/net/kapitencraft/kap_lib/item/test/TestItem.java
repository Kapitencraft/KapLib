package net.kapitencraft.kap_lib.item.test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.inventory.wearable.WearableItem;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.item.ExtendedItem;
import net.kapitencraft.kap_lib.registry.TestCooldowns;
import net.kapitencraft.kap_lib.registry.custom.WearableSlots;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends WearableItem implements ExtendedItem {

    public TestItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(WearableSlot slot, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (slot.is(WearableSlots.BELT)) {
            multimap.put(Attributes.LUCK, new AttributeModifier(BaseAttributeUUIDs.LUCK, "Lucky Belt Modifier", 10, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public WearableSlot getSlot() {
        return WearableSlots.BELT.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        if (TestCooldowns.TEST.get().isActive(pPlayer)) {
            pPlayer.sendSystemMessage(Component.literal("not work"));
        } else {
            if (!pLevel.isClientSide()) TestCooldowns.TEST.get().applyCooldown(pPlayer, false);
            pPlayer.sendSystemMessage(Component.literal("started"));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverTextWithPlayer(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag, Player player) {
        list.add(TestCooldowns.TEST.get().createDisplay(player));
    }
}
