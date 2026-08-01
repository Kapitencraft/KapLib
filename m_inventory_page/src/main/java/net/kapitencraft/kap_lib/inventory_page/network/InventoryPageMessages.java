package net.kapitencraft.kap_lib.inventory_page.network;

import net.kapitencraft.kap_lib.inventory_page.InventoryPageModule;
import net.kapitencraft.kap_lib.inventory_page.network.S2C.SyncWearablesToPlayerPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = InventoryPageModule.MODULE_ID)
public class InventoryPageMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncWearablesToPlayerPacket.TYPE, SyncWearablesToPlayerPacket.CODEC, SyncWearablesToPlayerPacket::handle);
    }
}
