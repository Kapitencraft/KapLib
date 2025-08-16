package net.kapitencraft.kap_lib.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class ShimmerShieldParticleOptions extends ParticleType<ShimmerShieldParticleOptions> implements ParticleOptions {
    private static final MapCodec<ShimmerShieldParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("minLifeTime").forGetter(ShimmerShieldParticleOptions::getMinLifeTime),
            Codec.INT.fieldOf("maxElements").forGetter(ShimmerShieldParticleOptions::getMaxElements),
            Codec.INT.fieldOf("entityId").forGetter(ShimmerShieldParticleOptions::getEntityId),
            Codec.INT.fieldOf("minRegenTime").forGetter(ShimmerShieldParticleOptions::getMinRegenTime),
            Codec.INT.fieldOf("maxRegenTime").forGetter(ShimmerShieldParticleOptions::getMaxRegenTime),
            Codec.INT.fieldOf("maxLifeTime").forGetter(ShimmerShieldParticleOptions::getMaxLifeTime),
            Color.CODEC.fieldOf("minColor").forGetter(ShimmerShieldParticleOptions::getMinColor),
            Color.CODEC.fieldOf("maxColor").forGetter(ShimmerShieldParticleOptions::getMaxColor),
            Codec.FLOAT.fieldOf("maxSpeed").forGetter(ShimmerShieldParticleOptions::getMaxSpeed),
            UUIDUtil.STRING_CODEC.fieldOf("uuid").forGetter(ShimmerShieldParticleOptions::getUUID)
            ).apply(instance, ShimmerShieldParticleOptions::new) //that's a lot
    );
    public static final StreamCodec<ByteBuf, ShimmerShieldParticleOptions> STREAM_CODEC = ExtraStreamCodecs.composite(
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getMinLifeTime,
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getMaxElements,
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getEntityId,
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getMinRegenTime,
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getMaxRegenTime,
            ByteBufCodecs.INT, ShimmerShieldParticleOptions::getMaxLifeTime,
            Color.STREAM_CODEC, ShimmerShieldParticleOptions::getMinColor,
            Color.STREAM_CODEC, ShimmerShieldParticleOptions::getMaxColor,
            ByteBufCodecs.FLOAT, ShimmerShieldParticleOptions::getMaxSpeed,
            ExtraStreamCodecs.UUID, ShimmerShieldParticleOptions::getUUID,
            ShimmerShieldParticleOptions::new
    );

    private final int minLifeTime, maxElements, entityId, minRegenTime, maxRegenTime, maxLifeTime;
    private final Color min, max;
    private final float maxSpeed;
    private final UUID uuid;

    public ShimmerShieldParticleOptions(int minLifeTime, int maxElements, int entityId, int minRegenTime, int maxRegenTime, int maxLifeTime, Color min, Color max, float maxSpeed, UUID uuid) {
        super(true);
        this.minLifeTime = minLifeTime;
        this.maxElements = maxElements;
        this.entityId = entityId;
        this.minRegenTime = minRegenTime;
        this.maxRegenTime = maxRegenTime;
        this.maxLifeTime = maxLifeTime;
        this.min = min;
        this.max = max;
        this.maxSpeed = maxSpeed;
        this.uuid = uuid;
    }

    @Override
    public ParticleType<?> getType() {
        return ExtraParticleTypes.SHIMMER_SHIELD.get();
    }

    @Override
    public MapCodec<ShimmerShieldParticleOptions> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ShimmerShieldParticleOptions> streamCodec() {
        return STREAM_CODEC;
    }

    public int getMinLifeTime() {
        return minLifeTime;
    }

    public Color getMinColor() {
        return min;
    }

    public Color getMaxColor() {
        return max;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getMaxElements() {
        return maxElements;
    }

    public int getMinRegenTime() {
        return minRegenTime;
    }

    public int getMaxRegenTime() {
        return maxRegenTime;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public int getMaxLifeTime() {
        return maxLifeTime;
    }

    public UUID getUUID() {
        return uuid;
    }
}
