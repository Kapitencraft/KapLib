package net.kapitencraft.kap_lib.event;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.component.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.inventory_page.registry.custom.InventoryPageRegistries;
import net.kapitencraft.kap_lib.inventory_page.wearable.IWearable;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ActivationTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        ActivationTriggers.ENTITY_ADDED.get().trigger(event.getEntity().getId());
    }

    @SubscribeEvent
    public static void onAddAttributeTooltips(AddAttributeTooltipsEvent event) {
        ItemStack stack = event.getStack();
        if (stack.getItem() instanceof IWearable wearable) {
            for (Map.Entry<ResourceKey<WearableSlot>, WearableSlot> slotEntry : InventoryPageRegistries.WEARABLE_SLOTS.entrySet()) {
                Multimap<Holder<Attribute>, AttributeModifier> modifiers = wearable.getModifiers(slotEntry.getValue(), stack);
                if (modifiers.isEmpty()) continue;
                event.addTooltipLines(CommonComponents.EMPTY, Component.translatable("item.modifiers.wearable." + getWearableKey(slotEntry.getKey())).withStyle(ChatFormatting.GRAY));
                AttributeUtil.applyTextFor(stack, event::addTooltipLines, modifiers, event.getContext());
            }
        }
    }

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event) {
        PlayerHeadAllocator.getInstance().shutDown();
    }

    @Unique
    private static String getWearableKey(ResourceKey<WearableSlot> key) {
        ResourceLocation location = key.location();
        return location.getNamespace() + "." + location.getPath();
    }
}
