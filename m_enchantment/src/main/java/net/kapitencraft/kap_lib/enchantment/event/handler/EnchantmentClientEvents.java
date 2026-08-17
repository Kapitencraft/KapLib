package net.kapitencraft.kap_lib.enchantment.event.handler;

import net.kapitencraft.kap_lib.core.event.custom.client.FontSetsEvent;
import net.kapitencraft.kap_lib.enchantment.EnchantmentModule;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_applicable.EnchantmentApplicableAllocator;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_color.ConfigureEnchantmentColorsCommand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = EnchantmentModule.MODULE_ID)
public class EnchantmentClientEvents {

    @SubscribeEvent
    public static void onFontSetsRegister(FontSetsEvent.Register event) {
        event.register(EnchantmentApplicableAllocator.FONT, new EnchantmentApplicableAllocator(event.getManager()));
    }

    @SubscribeEvent
    public static void onFontSetsUpdate(FontSetsEvent.Update event) {
        event.register(EnchantmentApplicableAllocator.FONT, EnchantmentApplicableAllocator.getInstance());
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ConfigureEnchantmentColorsCommand.register(event.getDispatcher());
    }
}
