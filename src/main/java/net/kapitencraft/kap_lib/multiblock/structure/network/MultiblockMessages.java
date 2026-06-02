package net.kapitencraft.kap_lib.multiblock.structure.network;

import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.network.S2C.UpdateBonusDataPacket;
import net.kapitencraft.kap_lib.multiblock.structure.network.C2S.SetMultiblockStructureConfigurationBlockDataPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class MultiblockMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SetMultiblockStructureConfigurationBlockDataPacket.TYPE, SetMultiblockStructureConfigurationBlockDataPacket.STREAM_CODEC, SetMultiblockStructureConfigurationBlockDataPacket::handle);
        registrar.playToClient(SyncBonusesPacket.TYPE, SyncBonusesPacket.CODEC, SyncBonusesPacket::handle);
        registrar.playToClient(UpdateBonusDataPacket.TYPE, UpdateBonusDataPacket.CODEC, UpdateBonusDataPacket::handle);
    }
}
