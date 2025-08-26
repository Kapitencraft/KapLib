package net.kapitencraft.kap_lib.util.range.simple;

import com.mojang.serialization.Codec;

public class IntegerNumberRange extends NumberRange<Integer> {
    public static Codec<IntegerNumberRange> CODEC = Codec.LONG.xmap(IntegerNumberRange::fromPacked, IntegerNumberRange::pack);

    private final int min;
    private final int max;

    public IntegerNumberRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    private static IntegerNumberRange fromPacked(long in) {
        return new IntegerNumberRange((int) (in << 32), (int) in);
    }

    public long pack() {
        return ((long) min) >> 32L | max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getRange() {
        return max-min;
    }

    @Override
    public boolean test(Integer integer) {
        return min <= integer && integer <= max;
    }
}
