package net.kapitencraft.kap_lib.enchantment.event.handler;

import net.kapitencraft.kap_lib.core.event.custom.client.FontSetsEvent;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_applicable.EnchantmentApplicableAllocator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EnchantmentClientEvents {

    @SubscribeEvent
    public static void onFontSetsRegister(FontSetsEvent.Register event) {
        event.register(EnchantmentApplicableAllocator.FONT, new EnchantmentApplicableAllocator(event.getManager()));
    }

    @SubscribeEvent
    public static void onFontSetsUpdate(FontSetsEvent.Update event) {
        event.register(EnchantmentApplicableAllocator.FONT, EnchantmentApplicableAllocator.getInstance());
    }
}
