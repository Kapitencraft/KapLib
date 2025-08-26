package net.kapitencraft.kap_lib.util.particle_help;

import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public record ParticleAmountHolder(ParticleOptions particleType, int amount) {

    public static <T extends ParticleOptions> ParticleAmountHolder fromNW(FriendlyByteBuf buf) {
        ParticleOptions options = NetworkHelper.readParticleOptions(buf);
        int amount = buf.readInt();
        return new ParticleAmountHolder(options, amount);
    }

    public void toNW(FriendlyByteBuf buf) {
        NetworkHelper.writeParticleOptions(buf, this.particleType);
        buf.writeInt(amount);
    }
}
