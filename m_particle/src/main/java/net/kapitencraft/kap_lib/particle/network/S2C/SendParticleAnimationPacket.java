package net.kapitencraft.kap_lib.particle.network.S2C;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimationManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

//run directly?
public record SendParticleAnimationPacket(ParticleAnimation animation) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SendParticleAnimationPacket> CODEC = ParticleAnimation.CODEC.map(SendParticleAnimationPacket::new, SendParticleAnimationPacket::animation);

    public static final Type<SendParticleAnimationPacket> TYPE = new Type<>(LibConstants.res("send_particle_animation"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> ParticleAnimationManager.INSTANCE.accept(animation));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
