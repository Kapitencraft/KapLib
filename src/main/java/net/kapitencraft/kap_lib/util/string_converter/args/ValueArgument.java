package net.kapitencraft.kap_lib.util.string_converter.args;

public record ValueArgument<T>(T value) implements TransferArg<T> {

    public static <T> ValueArgument<T> create(CalculationArgument<T> calcArg, ValueArgument<T> arg1, ValueArgument<T> arg2) {
        return new ValueArgument<>(calcArg.apply(arg1.value, arg2.value));
    }

    @Override
    public T apply(T value1, T value2) {
        return null;
    }
}