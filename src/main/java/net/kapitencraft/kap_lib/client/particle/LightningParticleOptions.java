package net.kapitencraft.kap_lib.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LightningParticleOptions extends ParticleType<LightningParticleOptions> implements ParticleOptions {
    private static final Codec<LightningParticleOptions> CODEC = RecordCodecBuilder.create(lightningParticleOptionsInstance -> lightningParticleOptionsInstance.group(
            Vec3.CODEC.fieldOf("start").forGetter(LightningParticleOptions::getStart),
            Vec3.CODEC.fieldOf("end").forGetter(LightningParticleOptions::getEnd)

    ).apply(lightningParticleOptionsInstance, LightningParticleOptions::new));

    public Vec3 getStart() {
        return start;
    }

    public Vec3 getEnd() {
        return end;
    }

    private final Vec3 start, end;

    public LightningParticleOptions(Vec3 start, Vec3 end) {
        super(true, new Deserializer());
        this.start = start;
        this.end = end;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ExtraParticleTypes.LIGHTNING.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        NetworkHelper.writeVec3(pBuffer, this.start);
        NetworkHelper.writeVec3(pBuffer, this.end);
    }

    @Override
    public String writeToString() {
        return "";
    }

    @Override
    public Codec<LightningParticleOptions> codec() {
        return CODEC;
    }

    private static class Deserializer implements ParticleOptions.Deserializer<LightningParticleOptions> {

        @Override
        public LightningParticleOptions fromCommand(ParticleType<LightningParticleOptions> pParticleType, StringReader pReader) throws CommandSyntaxException {

            return null;
        }

        @Override
        public LightningParticleOptions fromNetwork(ParticleType<LightningParticleOptions> pParticleType, FriendlyByteBuf pBuffer) {
            Vec3 start = NetworkHelper.readVec3(pBuffer);
            Vec3 end = NetworkHelper.readVec3(pBuffer);
            return new LightningParticleOptions(start, end);
        }
    }
}
