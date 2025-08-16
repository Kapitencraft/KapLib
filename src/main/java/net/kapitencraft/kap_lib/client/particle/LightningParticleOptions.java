package net.kapitencraft.kap_lib.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class LightningParticleOptions extends ParticleType<LightningParticleOptions> implements ParticleOptions {
    private static final MapCodec<LightningParticleOptions> CODEC = RecordCodecBuilder.mapCodec(lightningParticleOptionsInstance -> lightningParticleOptionsInstance.group(
            Vec3.CODEC.fieldOf("start").forGetter(LightningParticleOptions::getStart),
            Vec3.CODEC.fieldOf("end").forGetter(LightningParticleOptions::getEnd),
            Codec.INT.fieldOf("segments").forGetter(LightningParticleOptions::getSegments),
            Codec.INT.fieldOf("lifetime").forGetter(LightningParticleOptions::getLifetime),
            Codec.FLOAT.fieldOf("displacement").forGetter(LightningParticleOptions::getDisplacement),
            Codec.FLOAT.fieldOf("width").forGetter(LightningParticleOptions::getWidth)
    ).apply(lightningParticleOptionsInstance, LightningParticleOptions::new));
    private static final StreamCodec<FriendlyByteBuf, LightningParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.VEC_3, LightningParticleOptions::getStart,
            ExtraStreamCodecs.VEC_3, LightningParticleOptions::getEnd,
            ByteBufCodecs.INT, LightningParticleOptions::getSegments,
            ByteBufCodecs.INT, LightningParticleOptions::getLifetime,
            ByteBufCodecs.FLOAT, LightningParticleOptions::getDisplacement,
            ByteBufCodecs.FLOAT, LightningParticleOptions::getWidth,
            LightningParticleOptions::new
    );

    private final Vec3 start, end;
    private final int segments, lifetime;
    private final float displacement, width;

    public LightningParticleOptions(Vec3 start, Vec3 end, @Range(from = 2, to = 255) int segments, int lifetime, float displacement, float width) {
        super(true);
        this.start = start;
        this.end = end;
        this.segments = segments;
        this.lifetime = lifetime;
        this.displacement = displacement;
        this.width = width;
    }

    public Vec3 getStart() {
        return start;
    }

    public Vec3 getEnd() {
        return end;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ExtraParticleTypes.LIGHTNING.get();
    }

    @Override
    public @NotNull MapCodec<LightningParticleOptions> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, LightningParticleOptions> streamCodec() {
        return STREAM_CODEC;
    }

    public int getSegments() {
        return segments;
    }

    public float getDisplacement() {
        return displacement;
    }

    public float getWidth() {
        return width;
    }

    public int getLifetime() {
        return lifetime;
    }
}
