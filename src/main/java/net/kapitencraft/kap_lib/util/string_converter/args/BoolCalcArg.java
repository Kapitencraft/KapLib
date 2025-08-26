package net.kapitencraft.kap_lib.util.string_converter.args;

import net.kapitencraft.kap_lib.util.string_converter.converter.TextToBoolConverter;

public class BoolCalcArg implements CalculationArgument<Boolean> {
    private final String name;

    public BoolCalcArg(String name) {
        this.name = name;
    }

    @Override
    public Boolean apply(Boolean value1, Boolean value2) {
        return null;
    }

    @Override
    public boolean isPreferred() {
        return TextToBoolConverter.PREFERRED_ARGS.contains(this.name);
    }
}
