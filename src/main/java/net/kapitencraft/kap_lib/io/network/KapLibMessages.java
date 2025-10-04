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
public class KapLibMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncRequirementsPacket.TYPE, SyncRequirementsPacket.STREAM_CODEC, SyncRequirementsPacket::handle);
        registrar.playToClient(SyncBonusesPacket.TYPE, SyncBonusesPacket.CODEC, SyncBonusesPacket::handle);
        registrar.playToClient(DisplayTotemActivationPacket.TYPE, DisplayTotemActivationPacket.CODEC, DisplayTotemActivationPacket::handle);
        registrar.playToClient(SendParticleAnimationPacket.TYPE, SendParticleAnimationPacket.CODEC, SendParticleAnimationPacket::handle);
        registrar.playToClient(SendTrackingShotPacket.TYPE, SendTrackingShotPacket.CODEC, SendTrackingShotPacket::handle);
        registrar.playToClient(ActivateShakePacket.TYPE, ActivateShakePacket.CODEC, ActivateShakePacket::handle);
        registrar.playToClient(SyncWearablesToPlayerPacket.TYPE, SyncWearablesToPlayerPacket.CODEC, SyncWearablesToPlayerPacket::handle);
        registrar.playToClient(SyncCooldownsToPlayerPacket.TYPE, SyncCooldownsToPlayerPacket.CODEC, SyncCooldownsToPlayerPacket::handle);
        registrar.playToClient(CooldownStartedPacket.TYPE, CooldownStartedPacket.CODEC, CooldownStartedPacket::handle);
        registrar.playToClient(UpdateBonusDataPacket.TYPE, UpdateBonusDataPacket.CODEC, UpdateBonusDataPacket::handle);
    }
}