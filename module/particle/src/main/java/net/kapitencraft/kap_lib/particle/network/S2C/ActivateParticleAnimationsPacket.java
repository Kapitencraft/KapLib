package net.kapitencraft.kap_lib.particle.network.S2C;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ActivateParticleAnimationsPacket(List<ParticleAnimation> animations) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateParticleAnimationsPacket> CODEC = ParticleAnimation.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ActivateParticleAnimationsPacket::new, ActivateParticleAnimationsPacket::animations);

    public static final Type<ActivateParticleAnimationsPacket> TYPE = new Type<>(LibConstants.res("activate_particle_animations"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ignoredContext) {
        ClientParticleAnimationManager.activate(this.animations);
    }
}
