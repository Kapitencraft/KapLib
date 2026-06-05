package net.kapitencraft.kap_lib.network;

import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.network.S2C.UpdateBonusDataPacket;
import net.kapitencraft.kap_lib.camera.network.S2C.ActivateShakePacket;
import net.kapitencraft.kap_lib.camera.network.S2C.SendTrackingShotPacket;
import net.kapitencraft.kap_lib.core.network.S2C.DisplayTotemActivationPacket;
import net.kapitencraft.kap_lib.cooldown.network.S2C.CooldownStartedPacket;
import net.kapitencraft.kap_lib.cooldown.network.S2C.SyncCooldownsToPlayerPacket;
import net.kapitencraft.kap_lib.inventory_page.network.S2C.SyncWearablesToPlayerPacket;
import net.kapitencraft.kap_lib.particle.network.S2C.ActivateParticleAnimationsPacket;
import net.kapitencraft.kap_lib.particle.network.S2C.SendParticleAnimationPacket;
import net.kapitencraft.kap_lib.particle.network.S2C.UseParticleAnimationPresetPacket;
import net.kapitencraft.kap_lib.requirement.network.S2C.SyncRequirementsPacket;
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
        registrar.playToClient(ActivateParticleAnimationsPacket.TYPE, ActivateParticleAnimationsPacket.CODEC, ActivateParticleAnimationsPacket::handle);
        registrar.playToClient(UseParticleAnimationPresetPacket.TYPE, UseParticleAnimationPresetPacket.STREAM_CODEC, UseParticleAnimationPresetPacket::handle);
        registrar.playToClient(SendTrackingShotPacket.TYPE, SendTrackingShotPacket.CODEC, SendTrackingShotPacket::handle);
        registrar.playToClient(ActivateShakePacket.TYPE, ActivateShakePacket.CODEC, ActivateShakePacket::handle);
        registrar.playToClient(SyncWearablesToPlayerPacket.TYPE, SyncWearablesToPlayerPacket.CODEC, SyncWearablesToPlayerPacket::handle);
        registrar.playToClient(SyncCooldownsToPlayerPacket.TYPE, SyncCooldownsToPlayerPacket.CODEC, SyncCooldownsToPlayerPacket::handle);
        registrar.playToClient(CooldownStartedPacket.TYPE, CooldownStartedPacket.CODEC, CooldownStartedPacket::handle);
        registrar.playToClient(UpdateBonusDataPacket.TYPE, UpdateBonusDataPacket.CODEC, UpdateBonusDataPacket::handle);
    }
}