package net.kapitencraft.kap_lib.util.particle_help;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public record ParticleAmountHolder(ParticleOptions particleType, int amount) {

    public static <T extends ParticleOptions> ParticleAmountHolder fromNW(FriendlyByteBuf buf) {
        ParticleType<T> type = (ParticleType<T>) buf.readById(BuiltInRegistries.PARTICLE_TYPE);
        ParticleOptions options = type.getDeserializer().fromNetwork(type, buf);
        int amount = buf.readInt();
        return new ParticleAmountHolder(options, amount);
    }

    public void toNW(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particleType.getType());
        particleType.writeToNetwork(buf);
        buf.writeInt(amount);
    }
}
