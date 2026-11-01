package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record Quantifier(int minCount, int maxCount, int fromPosition, int toPosition, String name) {
    public static final Codec<Quantifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("minCount", 0).forGetter(Quantifier::minCount),
            Codec.INT.optionalFieldOf("maxCount", -1).forGetter(Quantifier::maxCount),
            Codec.INT.fieldOf("fromPosition").forGetter(Quantifier::fromPosition),
            Codec.INT.fieldOf("toPosition").forGetter(Quantifier::toPosition),
            Codec.STRING.optionalFieldOf("name", "").forGetter(Quantifier::name)
    ).apply(i, Quantifier::new));

    public static int findQuantifierIndex(int offset, List<Quantifier> quantifiers) {
        for (int i = 0; i < quantifiers.size(); i++) {
            Quantifier quantifier = quantifiers.get(i);
            if (quantifier.fromPosition() == offset && quantifier.toPosition() >= offset) {
                return i;
            }
        }
        return -1;
    }

    public static Quantifier findQuantifier(int offset, List<Quantifier> quantifiers) {
        for (Quantifier quantifier : quantifiers) {
            if (quantifier.fromPosition() == offset && quantifier.toPosition() >= offset) {
                return quantifier;
            }
        }
        return null;
    }

    public int length() {
        return toPosition - fromPosition + 1;
    }
}