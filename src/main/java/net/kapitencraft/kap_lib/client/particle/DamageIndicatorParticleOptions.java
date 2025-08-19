package net.kapitencraft.kap_lib.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * particle Options for {@link DamageIndicatorParticle}
 */
public class DamageIndicatorParticleOptions extends ParticleType<DamageIndicatorParticleOptions> implements ParticleOptions {
    private static final Codec<DamageIndicatorParticleOptions> CODEC = RecordCodecBuilder.create(optionsInstance ->
            optionsInstance.group(
                    Codec.INT.fieldOf("damageType")
                            .forGetter(DamageIndicatorParticleOptions::getDamageType),
                    Codec.FLOAT.fieldOf("damage")
                            .forGetter(DamageIndicatorParticleOptions::getDamage),
                    Codec.FLOAT.fieldOf("rangeOffset")
                            .forGetter(DamageIndicatorParticleOptions::getRangeOffset)
            ).apply(optionsInstance, DamageIndicatorParticleOptions::new));
    private final int damageType;
    private final float damage;
    private final float rangeOffset;

    public DamageIndicatorParticleOptions(int damageType, float damage, float rangeOffset) {
        super(true, new Deserializer());
        this.damageType = damageType;
        this.damage = damage;
        this.rangeOffset = rangeOffset;
    }

    public int getDamageType() {
        return damageType;
    }

    public float getDamage() {
        return damage;
    }

    public float getRangeOffset() {
        return rangeOffset;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ExtraParticleTypes.DAMAGE_INDICATOR.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(damageType);
        buf.writeFloat(damage);
        buf.writeFloat(rangeOffset);
    }

    @Override
    public @NotNull String writeToString() {
        return String.format("%s %d", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), damageType);
    }

    @Override
    public @NotNull Codec<DamageIndicatorParticleOptions> codec() {
        return CODEC;
    }

    public static class Deserializer implements ParticleOptions.Deserializer<DamageIndicatorParticleOptions> {

        @Override
        public @NotNull DamageIndicatorParticleOptions fromCommand(@NotNull ParticleType<DamageIndicatorParticleOptions> p_123733_, @NotNull StringReader reader) throws CommandSyntaxException {
            int damageType = reader.readInt();
            reader.expect(' ');
            float damage = reader.readFloat();
            reader.expect(' ');
            float rangeOffset = reader.readFloat();
            return new DamageIndicatorParticleOptions(damageType, damage, rangeOffset);
        }

        @Override
        public @NotNull DamageIndicatorParticleOptions fromNetwork(@NotNull ParticleType<DamageIndicatorParticleOptions> p_123735_, @NotNull FriendlyByteBuf buf) {
            int damageType = buf.readInt();
            float damage = buf.readFloat();
            float rangeOffset = buf.readFloat();
            return new DamageIndicatorParticleOptions(damageType, damage,  rangeOffset);
        }
    }
}
