package net.kapitencraft.kap_lib.particle.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseParticleAnimationPresetPacket(ResourceLocation location,
                                               ParticleAnimationPresetContext context) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, UseParticleAnimationPresetPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, UseParticleAnimationPresetPacket::location,
            ParticleAnimationPresetContext.STREAM_CODEC, UseParticleAnimationPresetPacket::context,
            UseParticleAnimationPresetPacket::new
    );

    public static final Type<UseParticleAnimationPresetPacket> TYPE = new Type<>(KapLibMod.res("use_particle_animation_preset"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        ClientParticleAnimationManager.INSTANCE.usePreset(this.location, this.context);
    }
}
