package net.kapitencraft.kap_lib.core.string_converter.args;

import net.kapitencraft.kap_lib.core.stream.UnaryBiOperator;

public interface CalculationArgument<T> extends TransferArg<T>, UnaryBiOperator<T> {
    boolean isPreferred();
}
