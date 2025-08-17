package net.kapitencraft.kap_lib.util.particle_help;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ParticleAmountHolder(ParticleOptions particleType, int amount) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleAmountHolder> STREAM_CODEC = StreamCodec.of((buffer, value) -> value.toNW(buffer), ParticleAmountHolder::fromNW);

    public static ParticleAmountHolder fromNW(RegistryFriendlyByteBuf buf) {
        ParticleOptions options = ParticleTypes.STREAM_CODEC.decode(buf);
        int amount = buf.readInt();
        return new ParticleAmountHolder(options, amount);
    }

    public void toNW(RegistryFriendlyByteBuf buf) {
        ParticleTypes.STREAM_CODEC.encode(buf, this.particleType);
        buf.writeInt(amount);
    }
}
