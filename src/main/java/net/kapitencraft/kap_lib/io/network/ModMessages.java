package net.kapitencraft.kap_lib.io.network;

import net.kapitencraft.kap_lib.io.network.S2C.*;
import net.kapitencraft.kap_lib.io.network.S2C.capability.CooldownStartedPacket;
import net.kapitencraft.kap_lib.io.network.S2C.capability.SyncCooldownsToPlayerPacket;
import net.kapitencraft.kap_lib.io.network.S2C.capability.SyncWearablesToPlayerPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class ModMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToClient();
        registrar.commonToClient(SyncBonusesPacket.TYPE, SyncBonusesPacket.CODEC, SyncBonusesPacket::handle);
        registrar.commonToClient(DisplayTotemActivationPacket.TYPE, DisplayTotemActivationPacket.CODEC, DisplayTotemActivationPacket::handle);
        registrar.commonToClient(SendParticleAnimationPacket.TYPE, SendParticleAnimationPacket.CODEC, SendParticleAnimationPacket::handle);
        registrar.commonToClient(SendTrackingShotPacket.TYPE, SendTrackingShotPacket.CODEC, SendTrackingShotPacket::handle);
        registrar.commonToClient(ActivateShakePacket.TYPE, ActivateShakePacket.CODEC, ActivateShakePacket::handle);
        registrar.commonToClient(SyncWearablesToPlayerPacket.TYPE, SyncWearablesToPlayerPacket.CODEC, SyncWearablesToPlayerPacket::handle);
        registrar.commonToClient(SyncCooldownsToPlayerPacket.TYPE, SyncCooldownsToPlayerPacket.CODEC, SyncCooldownsToPlayerPacket::handle);
        registrar.commonToClient(CooldownStartedPacket.TYPE, CooldownStartedPacket.CODEC, CooldownStartedPacket::handle);
        registrar.commonToClient(UpdateBonusDataPacket.TYPE, UpdateBonusDataPacket.CODEC, UpdateBonusDataPacket::handle);
        addMessage(SyncRequirementsPacket.class, NetworkDirection.PLAY_TO_CLIENT, SyncRequirementsPacket::new);
    }
}