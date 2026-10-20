package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Quantifier(int minCount, int maxCount, int fromPosition, int toPosition) {
    public static final Codec<Quantifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("minCount", 0).forGetter(Quantifier::minCount),
            Codec.INT.optionalFieldOf("maxCount", -1).forGetter(Quantifier::maxCount),
            Codec.INT.fieldOf("fromPosition").forGetter(Quantifier::fromPosition),
            Codec.INT.fieldOf("toPosition").forGetter(Quantifier::toPosition)
    ).apply(i, Quantifier::new));
}