package net.kapitencraft.kap_lib.core.string_converter.args.integers;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.converter.TextToNumConverter;

public record IntegerMathArgument(String type) implements Operation<Integer> {

    @Override
    public Integer apply(Integer a, Integer b) {
        return switch (type) {
            case "*" -> a * b;
            case "/" -> a / b;
            case "+" -> a + b;
            case "-" -> a - b;
            case "%" -> a % b;
            case "^" -> (int) Math.pow((double) a, (double) b);
            default -> 0;
        };
    }

    @Override
    public boolean isPreferred() {
        return TextToNumConverter.PRIORITY_ARGS.contains(type);
    }
}