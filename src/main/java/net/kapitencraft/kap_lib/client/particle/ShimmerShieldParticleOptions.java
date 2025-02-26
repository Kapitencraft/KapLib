package net.kapitencraft.kap_lib.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ShimmerShieldParticleOptions extends ParticleType<ShimmerShieldParticleOptions> implements ParticleOptions {
    private static final Codec<ShimmerShieldParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("minLifeTime").forGetter(ShimmerShieldParticleOptions::getMinLifeTime),
            Codec.INT.fieldOf("maxElements").forGetter(ShimmerShieldParticleOptions::getMaxElements),
            Codec.INT.fieldOf("entityId").forGetter(ShimmerShieldParticleOptions::getEntityId),
            Codec.INT.fieldOf("minRegenTime").forGetter(ShimmerShieldParticleOptions::getMinRegenTime),
            Codec.INT.fieldOf("maxRegenTime").forGetter(ShimmerShieldParticleOptions::getMaxRegenTime),
            Codec.INT.fieldOf("maxLifeTime").forGetter(ShimmerShieldParticleOptions::getMaxLifeTime),
            Color.CODEC.fieldOf("minColor").forGetter(ShimmerShieldParticleOptions::getMinColor),
            Color.CODEC.fieldOf("maxColor").forGetter(ShimmerShieldParticleOptions::getMaxColor),
            Codec.FLOAT.fieldOf("maxSpeed").forGetter(ShimmerShieldParticleOptions::getMaxSpeed),
            ExtraCodecs.UUID.fieldOf("uuid").forGetter(ShimmerShieldParticleOptions::getUUID)
            ).apply(instance, ShimmerShieldParticleOptions::new) //that's a lot
    );

    private final int minLifeTime, maxElements, entityId, minRegenTime, maxRegenTime, maxLifeTime;
    private final Color min, max;
    private final float maxSpeed;
    private final UUID uuid;

    public ShimmerShieldParticleOptions(int minLifeTime, int maxElements, int entityId, int minRegenTime, int maxRegenTime, int maxLifeTime, Color min, Color max, float maxSpeed, UUID uuid) {
        super(true, new Deserializer());
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
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(minLifeTime);
        pBuffer.writeInt(maxElements);
        pBuffer.writeInt(entityId);
        pBuffer.writeInt(minRegenTime);
        pBuffer.writeInt(maxRegenTime);
        pBuffer.writeInt(maxLifeTime);

        min.write(pBuffer);
        max.write(pBuffer);

        pBuffer.writeFloat(maxSpeed);

        pBuffer.writeUUID(uuid);
    }

    @Override
    public String writeToString() {
        return "";
    }

    @Override
    public Codec<ShimmerShieldParticleOptions> codec() {
        return CODEC;
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
        return null;
    }

    private static class Deserializer implements ParticleOptions.Deserializer<ShimmerShieldParticleOptions> {

        @Override
        public ShimmerShieldParticleOptions fromCommand(ParticleType<ShimmerShieldParticleOptions> pParticleType, StringReader pReader) throws CommandSyntaxException {
            return null;
        }

        @Override
        public ShimmerShieldParticleOptions fromNetwork(ParticleType<ShimmerShieldParticleOptions> pParticleType, FriendlyByteBuf pBuffer) {
            int minLifeTime = pBuffer.readInt(),
                    maxElements = pBuffer.readInt(),
                    entityId = pBuffer.readInt(),
                    minRegenTime = pBuffer.readInt(),
                    maxRegenTime = pBuffer.readInt(),
                    maxLifeTime = pBuffer.readInt();
            Color min = Color.read(pBuffer), max = Color.read(pBuffer);

            float maxSpeed = pBuffer.readFloat();

            UUID uuid = pBuffer.readUUID();
            return new ShimmerShieldParticleOptions(minLifeTime, maxElements, entityId, minRegenTime, maxRegenTime, maxLifeTime, min, max, maxSpeed, uuid);
        }
    }
}
