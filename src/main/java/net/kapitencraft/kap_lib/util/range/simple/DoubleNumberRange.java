package net.kapitencraft.kap_lib.util.range.simple;

public class DoubleNumberRange extends NumberRange<Double> {
    private final double min;
    private final double max;

    public DoubleNumberRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean test(Double aDouble) {
        return min <= aDouble && aDouble <= max;
    }
}
