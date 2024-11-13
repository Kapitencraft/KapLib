package net.kapitencraft.kap_lib.client.particle.animation;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * [WIP]
 */
public class ParticleAnimationOptions extends ParticleType<ParticleAnimationOptions> implements ParticleOptions {
    final ParticleOptions options;

    final ParticleAnimationParameters params;
    final ParticleAnimationInfo info;
    final int amount;

    public ParticleAnimationOptions(ParticleOptions options, ParticleAnimationParameters context, ParticleAnimationInfo info, int amount) {
        super(true, new Deserializer());
        this.options = options;
        this.params = context;
        this.info = info;
        this.amount = amount;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ExtraParticleTypes.ANIMATION.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
        options.writeToNetwork(buf);
        params.writeToNetwork(buf);
        info.writeToNetwork(buf);
        buf.writeInt(amount);
    }

    @Override
    public @NotNull String writeToString() {
        return options.writeToString();
    }

    @Override
    public @NotNull Codec<ParticleAnimationOptions> codec() {
        return Codec.unit(this);
    }

    public static class Deserializer implements ParticleOptions.Deserializer<ParticleAnimationOptions> {

        @Override
        public @NotNull ParticleAnimationOptions fromCommand(@NotNull ParticleType<ParticleAnimationOptions> p_123733_, @NotNull StringReader p_123734_) throws CommandSyntaxException {
            throw new IllegalAccessError("tried accessing animator via command");
        }

        @Override
        public @NotNull ParticleAnimationOptions fromNetwork(@NotNull ParticleType<ParticleAnimationOptions> type, FriendlyByteBuf buf) {
            ParticleType<?> type1 = buf.readById(BuiltInRegistries.PARTICLE_TYPE);
            ParticleOptions options = readParticle(buf, type1);
            ParticleAnimationParameters context = ParticleAnimationParameters.loadFromNetwork(buf);
            ParticleAnimationInfo info = ParticleAnimationInfo.loadFromNetwork(buf);
            return new ParticleAnimationOptions(options, context, info, buf.readInt());
        }
    }

    private static  <T extends ParticleOptions> T readParticle(FriendlyByteBuf p_132305_, @Nullable ParticleType<T> p_132306_) {
        return p_132306_ == null ? null : p_132306_.getDeserializer().fromNetwork(p_132306_, p_132305_);
    }
}
