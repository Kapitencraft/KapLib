package net.kapitencraft.kap_lib.core.string_converter;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;

import java.util.function.BiFunction;
import java.util.function.Function;

public record AlgebraOperationsStorage<T extends Number>(
        BiFunction<Operation<T>, Operation<T>, Operation<T>> add,
        BiFunction<Operation<T>, Operation<T>, Operation<T>> sub,
        BiFunction<Operation<T>, Operation<T>, Operation<T>> mul,
        BiFunction<Operation<T>, Operation<T>, Operation<T>> div,
        BiFunction<Operation<T>, Operation<T>, Operation<T>> mod,
        BiFunction<Operation<T>, Operation<T>, Operation<T>> pow,
        Function<String, T> literalExtractor,
        Function<String, Operation<T>> refExtractor
) {
}
