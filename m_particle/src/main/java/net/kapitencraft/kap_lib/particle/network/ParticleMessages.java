package net.kapitencraft.kap_lib.particle.network;

import net.kapitencraft.kap_lib.particle.network.S2C.SendParticleAnimationPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class ParticleMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SendParticleAnimationPacket.TYPE, SendParticleAnimationPacket.CODEC, SendParticleAnimationPacket::handle);
    }
}