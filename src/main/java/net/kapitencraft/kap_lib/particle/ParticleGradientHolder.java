package net.kapitencraft.kap_lib.particle;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ParticleGradientHolder(ParticleAmountHolder holder1, ParticleAmountHolder holder2) {
    private static final StreamCodec<? super RegistryFriendlyByteBuf, ParticleGradientHolder> STREAM_CODEC = StreamCodec.composite(
            ParticleAmountHolder.STREAM_CODEC, ParticleGradientHolder::holder1,
            ParticleAmountHolder.STREAM_CODEC, ParticleGradientHolder::holder2,
            ParticleGradientHolder::new
    );
}
