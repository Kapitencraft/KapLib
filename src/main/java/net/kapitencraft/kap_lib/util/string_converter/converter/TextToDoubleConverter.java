package net.kapitencraft.kap_lib.util.string_converter.converter;

import net.kapitencraft.kap_lib.util.string_converter.args.CalculationArgument;
import net.kapitencraft.kap_lib.util.string_converter.args.MathArgument;

public class TextToDoubleConverter extends TextToNumConverter<Double> {
    public TextToDoubleConverter(String args) {
        super(args, Double::valueOf);
    }

    @Override
    protected CalculationArgument<Double> getCalcArg(String value) {
        return new MathArgument<>(value);
    }
}
