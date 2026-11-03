package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.args.integers.IntegerMathArgument;

public class TextToIntConverter extends TextToNumConverter<Integer> {
    public static final Codec<TextToIntConverter> CODEC = createCodec(TextToIntConverter::new);

    public TextToIntConverter(String args) {
        super(args, Integer::valueOf);
    }

    @Override
    protected CalculationArgument<Integer> getCalcArg(String value) {
        return new IntegerMathArgument(value);
    }
}