package net.kapitencraft.kap_lib.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

/**
 * particle Options for {@link DamageIndicatorParticle}
 */
public class DamageIndicatorParticleOptions extends ParticleType<DamageIndicatorParticleOptions> implements ParticleOptions {
    private static final MapCodec<DamageIndicatorParticleOptions> CODEC = RecordCodecBuilder.mapCodec(optionsInstance ->
            optionsInstance.group(
                    Codec.INT.fieldOf("damageType")
                            .forGetter(DamageIndicatorParticleOptions::getDamageType),
                    Codec.FLOAT.fieldOf("damage")
                            .forGetter(DamageIndicatorParticleOptions::getDamage),
                    Codec.FLOAT.fieldOf("rangeOffset")
                            .forGetter(DamageIndicatorParticleOptions::getRangeOffset)
            ).apply(optionsInstance, DamageIndicatorParticleOptions::new));
    private static final StreamCodec<ByteBuf, DamageIndicatorParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DamageIndicatorParticleOptions::getDamageType,
            ByteBufCodecs.FLOAT, DamageIndicatorParticleOptions::getDamage,
            ByteBufCodecs.FLOAT, DamageIndicatorParticleOptions::getRangeOffset,
            DamageIndicatorParticleOptions::new
    );

    private final int damageType;
    private final float damage;
    private final float rangeOffset;

    public DamageIndicatorParticleOptions(int damageType, float damage, float rangeOffset) {
        super(true);
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
    public @NotNull MapCodec<DamageIndicatorParticleOptions> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, DamageIndicatorParticleOptions> streamCodec() {
        return STREAM_CODEC;
    }

}
