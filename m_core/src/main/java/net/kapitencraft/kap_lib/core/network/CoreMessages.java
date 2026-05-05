package net.kapitencraft.kap_lib.core.network;

import net.kapitencraft.kap_lib.core.network.S2C.DisplayTotemActivationPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class CoreMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(DisplayTotemActivationPacket.TYPE, DisplayTotemActivationPacket.CODEC, DisplayTotemActivationPacket::handle);
    }
}