package net.kapitencraft.kap_lib.util.string_converter.converter;

import java.util.List;
import java.util.function.Function;

public abstract class TextToNumConverter<T extends Number> extends TextConverter<T> {

    public TextToNumConverter(String args, Function<String, T> function) {
        super(function, args);
    }


    @Override
    protected boolean isArg(String s) {
        return PREFERRED_ARGS.contains(s) || OTHER_ARGS.contains(s);
    }
    public static final List<String> PREFERRED_ARGS = List.of("*", "/", "%");
    public static final List<String> OTHER_ARGS = List.of("+", "-");
}