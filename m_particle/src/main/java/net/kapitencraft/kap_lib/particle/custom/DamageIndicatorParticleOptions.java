package net.kapitencraft.kap_lib.particle.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.core.helpers.ParticleHelper;
import net.kapitencraft.kap_lib.particle.registry.ExtraParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
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

    @ApiStatus.Internal
    public static void create(LivingEntity entity, float amount, String type) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            float rangeOffset = entity.getBbHeight() / 2;
            ParticleHelper.sendParticles(serverLevel, new DamageIndicatorParticleOptions(damageIndicatorCoder(type), amount, rangeOffset), false, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
        }
    }

    //region color encoding
    @ApiStatus.Internal
    public static ChatFormatting damageIndicatorColorGenerator(String type) {
        return switch (type) {
            case "heal" -> ChatFormatting.DARK_GREEN;
            case "wither" -> ChatFormatting.BLACK;
            case "ferocity" -> ChatFormatting.GOLD;
            case "drown" -> ChatFormatting.AQUA;
            case "fire" -> ChatFormatting.DARK_RED;
            case "dodge" -> ChatFormatting.DARK_GRAY;
            default -> ChatFormatting.RED;
        };
    }

    @ApiStatus.Internal
    public static @NotNull ChatFormatting damageIndicatorColorFromDouble(double in) {
        return damageIndicatorColorGenerator(damageIndicatorDecoder(in));
    }

    @ApiStatus.Internal
    public static String damageIndicatorDecoder(double in) {
        return switch ((int) in) {
            case 1 -> "heal";
            case 2 -> "wither";
            case 3 -> "ferocity";
            case 4 -> "drown";
            case 5 -> "ability";
            case 6 -> "dodge";
            case 7 -> "fire";
            default -> "normal";
        };
    }

    @ApiStatus.Internal
    public static int damageIndicatorCoder(String id) {
        return switch (id) {
            case "heal" -> 1;
            case "wither" -> 2;
            case "ferocity" -> 3;
            case "drown" -> 4;
            case "ability" -> 5;
            case "dodge" -> 6;
            case "fire" -> 7;
            case "poison" -> 8;
            default -> 0;
        };
    }
    //endregion
}
