package net.kapitencraft.kap_lib.util.string_converter.args;

import net.kapitencraft.kap_lib.stream.UnaryBiOperator;

public interface CalculationArgument<T> extends TransferArg<T>, UnaryBiOperator<T> {
    boolean isPreferred();
}
