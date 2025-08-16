package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

//run directly?
public record SendParticleAnimationPacket(ParticleAnimation animation) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SendParticleAnimationPacket> CODEC = ParticleAnimation.CODEC.map(SendParticleAnimationPacket::new, SendParticleAnimationPacket::animation);

    public static final Type<SendParticleAnimationPacket> TYPE = new Type<>(KapLibMod.res("send_particle_animation"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> LibClient.animations.accept(animation));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
