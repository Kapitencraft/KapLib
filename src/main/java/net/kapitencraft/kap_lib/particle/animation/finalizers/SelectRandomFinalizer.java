package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import org.jetbrains.annotations.NotNull;

public class SelectRandomFinalizer implements ParticleFinalizer {
    private final WeightedRandomList<WeightedEntry.Wrapper<ParticleFinalizer>> entries;

    public SelectRandomFinalizer(WeightedRandomList<WeightedEntry.Wrapper<ParticleFinalizer>> entries) {
        this.entries = entries;
    }

    @Override
    public @NotNull Type getType() {
        return null;
    }

    @Override
    public void finalize(ParticleConfig config) {
        this.entries.getRandom(Minecraft.getInstance().level.random).orElseThrow().data().finalize(config);
    }

    public static class Type implements ParticleFinalizer.Type<SelectRandomFinalizer> {
        private static final StreamCodec<RegistryFriendlyByteBuf, WeightedEntry.Wrapper<ParticleFinalizer>> ENTRY_STREAM_CODEC = StreamCodec.composite(
                ParticleFinalizer.STREAM_CODEC, WeightedEntry.Wrapper::data,
                ByteBufCodecs.INT.map(Weight::of, Weight::asInt), WeightedEntry.Wrapper::weight,
                WeightedEntry.Wrapper::new
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, SelectRandomFinalizer> STREAM_CODEC =
                ENTRY_STREAM_CODEC.apply(ByteBufCodecs.list())
                        .map(WeightedRandomList::create, WeightedRandomList::unwrap)
                        .map(SelectRandomFinalizer::new, f -> f.entries);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SelectRandomFinalizer> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<SelectRandomFinalizer> codec() {
            return null;
        }
    }
}
