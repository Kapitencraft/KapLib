package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record SectionQuantifier(int minCount, int maxCount, int fromPosition, int toPosition, String name) {
    public static final Codec<SectionQuantifier> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("minCount", 0).forGetter(SectionQuantifier::minCount),
            Codec.INT.optionalFieldOf("maxCount", -1).forGetter(SectionQuantifier::maxCount),
            Codec.INT.fieldOf("fromPosition").forGetter(SectionQuantifier::fromPosition),
            Codec.INT.fieldOf("toPosition").forGetter(SectionQuantifier::toPosition),
            Codec.STRING.optionalFieldOf("name", "").forGetter(SectionQuantifier::name)
    ).apply(i, SectionQuantifier::new));

    public static int findQuantifierIndex(int offset, List<SectionQuantifier> sectionQuantifiers) {
        for (int i = 0; i < sectionQuantifiers.size(); i++) {
            SectionQuantifier sectionQuantifier = sectionQuantifiers.get(i);
            if (sectionQuantifier.fromPosition() == offset && sectionQuantifier.toPosition() >= offset) {
                return i;
            }
        }
        return -1;
    }

    public static SectionQuantifier findQuantifier(int offset, List<SectionQuantifier> sectionQuantifiers) {
        for (SectionQuantifier sectionQuantifier : sectionQuantifiers) {
            if (sectionQuantifier.fromPosition() == offset && sectionQuantifier.toPosition() >= offset) {
                return sectionQuantifier;
            }
        }
        return null;
    }

    public int length() {
        return toPosition - fromPosition + 1;
    }
}