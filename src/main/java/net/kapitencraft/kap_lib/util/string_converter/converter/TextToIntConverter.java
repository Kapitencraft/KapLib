package net.kapitencraft.kap_lib.util.string_converter.converter;

import net.kapitencraft.kap_lib.util.string_converter.args.CalculationArgument;
import net.kapitencraft.kap_lib.util.string_converter.args.MathArgument;

public class TextToIntConverter extends TextToNumConverter<Integer> {

    public TextToIntConverter(String args) {
        super(args, Integer::valueOf);
    }

    @Override
    protected CalculationArgument<Integer> getCalcArg(String value) {
        return new MathArgument<>(value);
    }
}