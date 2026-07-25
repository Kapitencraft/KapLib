package net.kapitencraft.kap_lib.component.event;

import net.kapitencraft.kap_lib.component.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.core.event.custom.client.FontSetsEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class ComponentClientEvents {

    @SubscribeEvent
    public static void onFontSetsRegister(FontSetsEvent.Register event) {
        event.register(PlayerHeadAllocator.FONT, new PlayerHeadAllocator(Minecraft.getInstance().getSkinManager(), event.getManager()));
    }

    @SubscribeEvent
    public static void onFontSetsUpdate(FontSetsEvent.Update event) {
        event.register(PlayerHeadAllocator.FONT, PlayerHeadAllocator.getInstance());
    }
}
