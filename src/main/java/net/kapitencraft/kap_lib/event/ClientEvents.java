package net.kapitencraft.kap_lib.event;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.inventory.wearable.IWearable;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ActivationTriggers;
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
    public void onAddAttributeTooltips(AddAttributeTooltipsEvent event) {
        ItemStack stack = event.getStack();
        if (stack.getItem() instanceof IWearable wearable) {
            for (Map.Entry<ResourceKey<WearableSlot>, WearableSlot> slotEntry : ExtraRegistries.WEARABLE_SLOTS.entrySet()) {
                Multimap<Holder<Attribute>, AttributeModifier> modifiers = wearable.getModifiers(slotEntry.getValue(), stack);
                if (modifiers.isEmpty()) continue;
                event.addTooltipLines(CommonComponents.EMPTY, Component.translatable("item.modifiers.wearable." + getWearableKey(slotEntry.getKey())).withStyle(ChatFormatting.GRAY));
                AttributeUtil.applyTextFor(stack, event::addTooltipLines, modifiers, event.getContext());
            }
        }
    }

    @Unique
    private static String getWearableKey(ResourceKey<WearableSlot> key) {
        ResourceLocation location = key.location();
        return location.getNamespace() + "." + location.getPath();
    }

}
