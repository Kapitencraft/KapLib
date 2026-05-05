package net.kapitencraft.kap_lib.requirement.network;

import net.kapitencraft.kap_lib.requirement.network.S2C.SyncRequirementsPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class RequirementMessages {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncRequirementsPacket.TYPE, SyncRequirementsPacket.STREAM_CODEC, SyncRequirementsPacket::handle);
    }
}