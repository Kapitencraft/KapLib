package net.kapitencraft.kap_lib.util.particle_help;

import net.minecraft.network.FriendlyByteBuf;

public record ParticleGradientHolder(ParticleAmountHolder holder1, ParticleAmountHolder holder2) {

    public static ParticleGradientHolder fromNW(FriendlyByteBuf buf) {
        return new ParticleGradientHolder(ParticleAmountHolder.fromNW(buf), ParticleAmountHolder.fromNW(buf));
    }

    public void toNW(FriendlyByteBuf buf) {
        holder1.toNW(buf);
        holder2.toNW(buf);
    }
}
