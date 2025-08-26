package net.kapitencraft.kap_lib.util.string_converter.args;

import net.kapitencraft.kap_lib.stream.UnaryBiOperator;
import net.kapitencraft.kap_lib.util.string_converter.converter.TextToNumConverter;

public class MathArgument<T extends Number> implements CalculationArgument<T> {
    private final String name;

    public MathArgument(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public T apply(T a, T b) {
        return switch (name) {
            case "*" -> MULTIPLY.apply(a, b);
            case "/" -> DIVIDE.apply(a, b);
            case "+" -> ADD.apply(a, b);
            case "-" -> REDUCE.apply(a, b);
            case "%" -> MODULO.apply(a, b);
            case "^" -> SQUARE.apply(a, b);
            default -> (T) (Number) 0;
        };
    }

    private final UnaryBiOperator<T> MULTIPLY = (value1, value2) -> (T) (Number) (((Double) value1) * ((Double) value2));
    private final UnaryBiOperator<T> SQUARE = (value1, value2) -> (T) (Number) Math.pow(((Double) value1), ((Double) value2));
    private final UnaryBiOperator<T> DIVIDE = (value1, value2) -> (T) (Number) (((Double) value1) / ((Double) value2));
    private final UnaryBiOperator<T> ADD = (value1, value2) -> (T) (Number) (((Double) value1) + ((Double) value2));
    private final UnaryBiOperator<T> REDUCE = (value1, value2) -> (T) (Number) (((Double) value1) - ((Double) value2));
    private final UnaryBiOperator<T> MODULO = (value1, value2) -> (T) (Number) (((Integer) value1) % ((Integer) value2));

    @Override
    public boolean isPreferred() {
        return TextToNumConverter.PREFERRED_ARGS.contains(name);
    }
}