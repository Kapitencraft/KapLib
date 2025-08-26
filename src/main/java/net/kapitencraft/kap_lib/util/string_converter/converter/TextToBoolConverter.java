package net.kapitencraft.kap_lib.util.string_converter.converter;

import net.kapitencraft.kap_lib.util.string_converter.args.BoolCalcArg;
import net.kapitencraft.kap_lib.util.string_converter.args.CalculationArgument;

import java.util.List;

public class TextToBoolConverter extends TextConverter<Boolean> {
    protected TextToBoolConverter(String args) {
        super(Boolean::valueOf, args);
    }

    @Override
    protected CalculationArgument<Boolean> getCalcArg(String value) {
        return new BoolCalcArg(value);
    }

    @Override
    protected boolean isArg(String s) {
        return PREFERRED_ARGS.contains(s) || OTHER_ARGS.contains(s);
    }

    public static List<String> PREFERRED_ARGS = List.of("==", "!=", "<=", ">=");
    public static List<String> OTHER_ARGS = List.of("||", "&&");
}
