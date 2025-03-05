package net.kapitencraft.kap_lib.client.enchantment_color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.util.range.simple.IntegerNumberRange;
import net.minecraft.util.Mth;

public class LevelRange extends IntegerNumberRange {
    static final Codec<LevelRange> CODEC = RecordCodecBuilder.create(levelRangeInstance ->
            levelRangeInstance.group(
                    Codec.LONG.fieldOf("packed").forGetter(IntegerNumberRange::pack),
                    Codec.BOOL.optionalFieldOf("relative", false).forGetter(LevelRange::isMaxLevelRelative)
            ).apply(levelRangeInstance, LevelRange::fromCodec)
    );

    private static LevelRange fromCodec(long in, Boolean relative) {
        return new LevelRange((int) (in << 32), (int) in, relative);
    }

    private final boolean maxLevelRelative;

    public LevelRange(int min, int max, boolean maxLevelRelative) {
        super(Mth.clamp(min, -255, 255), Mth.clamp(max, -255, 255));
        if (max < min) throw new IllegalArgumentException("min on level range smaller than max");
        this.maxLevelRelative = maxLevelRelative;
    }

    public boolean isMaxLevelRelative() {
        return maxLevelRelative;
    }

    public LevelRange setRelative(boolean relative) {
        return new LevelRange(this.getMin(), this.getMax(), relative);
    }

    public LevelRange withMin(int min) {
        return new LevelRange(min, this.getMax(), maxLevelRelative);
    }

    public LevelRange withMax(int max) {
        return new LevelRange(this.getMin(), max, maxLevelRelative);
    }

    public boolean test(int level, int maxLevel) {
        return super.test(maxLevelRelative ? level - maxLevel : level);
    }
}
