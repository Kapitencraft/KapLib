package net.kapitencraft.kap_lib.bonus.network;

import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.network.S2C.UpdateBonusDataPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class BonusModuleMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncBonusesPacket.TYPE, SyncBonusesPacket.CODEC, SyncBonusesPacket::handle);
        registrar.playToClient(UpdateBonusDataPacket.TYPE, UpdateBonusDataPacket.CODEC, UpdateBonusDataPacket::handle);
    }
}