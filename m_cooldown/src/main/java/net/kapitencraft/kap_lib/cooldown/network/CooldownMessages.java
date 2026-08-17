package net.kapitencraft.kap_lib.cooldown.network;

import net.kapitencraft.kap_lib.cooldown.CooldownModule;
import net.kapitencraft.kap_lib.cooldown.network.S2C.CooldownStartedPacket;
import net.kapitencraft.kap_lib.cooldown.network.S2C.SyncCooldownsToPlayerPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CooldownModule.MODULE_ID)
public class CooldownMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncCooldownsToPlayerPacket.TYPE, SyncCooldownsToPlayerPacket.CODEC, SyncCooldownsToPlayerPacket::handle);
        registrar.playToClient(CooldownStartedPacket.TYPE, CooldownStartedPacket.CODEC, CooldownStartedPacket::handle);
    }
}