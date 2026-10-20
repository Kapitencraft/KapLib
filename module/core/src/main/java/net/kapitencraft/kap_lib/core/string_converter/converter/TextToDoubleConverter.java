package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.args.CalculationArgument;
import net.kapitencraft.kap_lib.core.string_converter.args.MathArgument;

public class TextToDoubleConverter extends TextToNumConverter<Double> {
    public static final Codec<TextToDoubleConverter> CODEC = createCodec(TextToDoubleConverter::new);

    public TextToDoubleConverter(String args) {
        super(args, Double::valueOf);
    }

    @Override
    protected CalculationArgument<Double> getCalcArg(String value) {
        return new MathArgument<>(value);
    }
}
