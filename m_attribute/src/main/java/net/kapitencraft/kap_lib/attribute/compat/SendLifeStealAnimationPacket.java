package net.kapitencraft.kap_lib.attribute.compat;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.config.ParticleClientModConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendLifeStealAnimationPacket(ParticleAnimation animation) implements CustomPacketPayload {
    public static final Type<SendLifeStealAnimationPacket> TYPE = new Type<>(LibConstants.res("send_life_steal_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendLifeStealAnimationPacket> STREAM_CODEC = ParticleAnimation.STREAM_CODEC.map(SendLifeStealAnimationPacket::new, SendLifeStealAnimationPacket::animation);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        if (ParticleClientModConfig.lifeStealParticleEnabled())
            context.enqueueWork(() -> ClientParticleAnimationManager.INSTANCE.accept(animation));
    }
}
