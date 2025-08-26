package net.kapitencraft.kap_lib.util.string_converter.args;

import net.kapitencraft.kap_lib.stream.UnaryBiOperator;

public interface TransferArg<T> extends UnaryBiOperator<T> {
    default T value() {
        return (T) (Number) 0;
    }
}