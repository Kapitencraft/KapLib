package net.kapitencraft.kap_lib.particle.animation.finalizers;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.FinalizerTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectRandomFinalizer implements ParticleFinalizer {
    private final WeightedRandomList<WeightedEntry.Wrapper<ParticleFinalizer>> entries;

    public SelectRandomFinalizer(WeightedRandomList<WeightedEntry.Wrapper<ParticleFinalizer>> entries) {
        this.entries = entries;
    }

    @Override
    public @NotNull Type getType() {
        return FinalizerTypes.SELECT_RANDOM.get();
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
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder implements ParticleFinalizer.Builder<SelectRandomFinalizer> {
        private static final Codec<Pair<Integer, ParticleFinalizer.Builder<?>>> ENTRY_CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("weight").forGetter(Pair::getFirst),
                ParticleFinalizer.CODEC.fieldOf("value").forGetter(Pair::getSecond)
        ).apply(i, Pair::new));

        private static final MapCodec<Builder> CODEC = ENTRY_CODEC.listOf().xmap(Builder::fromCodec, Builder::toCodec).fieldOf("entries");

        private List<Pair<Integer, ParticleFinalizer.Builder<?>>> toCodec() {
            List<Pair<Integer, ParticleFinalizer.Builder<?>>> list = new ArrayList<>();
            for (WeightedEntry.Wrapper<ParticleFinalizer.Builder<?>> entry : this.entries) {
                list.add(new Pair<>(entry.weight().asInt(), entry.data()));
            }
            return list;
        }

        private static Builder fromCodec(List<Pair<Integer, ParticleFinalizer.Builder<?>>> list) {
            Builder builder = new Builder();
            for (Pair<Integer, ParticleFinalizer.Builder<?>> pair : list) {
                builder.addEntry(pair.getFirst(), pair.getSecond());
            }
            return builder;
        }

        private final List<WeightedEntry.Wrapper<ParticleFinalizer.Builder<?>>> entries = new ArrayList<>();

        public Builder addEntry(int weight, ParticleFinalizer.Builder<?> entry) {
            this.entries.add(new WeightedEntry.Wrapper<>(entry, Weight.of(weight)));
            return this;
        }

        @Override
        public SelectRandomFinalizer build(ParticleAnimationPresetContext context) {
            return new SelectRandomFinalizer(WeightedRandomList.create(entries.stream().map(w -> new WeightedEntry.Wrapper<>((ParticleFinalizer) w.data().build(context), w.getWeight())).toList()));
        }

        @Override
        public ParticleFinalizer.Type<SelectRandomFinalizer> type() {
            return FinalizerTypes.SELECT_RANDOM.get();
        }
    }
}
