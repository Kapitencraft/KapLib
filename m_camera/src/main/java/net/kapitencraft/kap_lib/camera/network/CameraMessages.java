package net.kapitencraft.kap_lib.camera.network;

import net.kapitencraft.kap_lib.camera.network.S2C.ActivateShakePacket;
import net.kapitencraft.kap_lib.camera.network.S2C.SendTrackingShotPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class CameraMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SendTrackingShotPacket.TYPE, SendTrackingShotPacket.CODEC, SendTrackingShotPacket::handle);
        registrar.playToClient(ActivateShakePacket.TYPE, ActivateShakePacket.CODEC, ActivateShakePacket::handle);
    }
}