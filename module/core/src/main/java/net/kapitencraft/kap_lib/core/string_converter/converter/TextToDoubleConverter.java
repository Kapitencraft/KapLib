package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.AlgebraOperationsStorage;
import net.kapitencraft.kap_lib.core.string_converter.AlgebraParser;
import net.kapitencraft.kap_lib.core.string_converter.Token;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.args.doubles.*;

import java.util.List;

public class TextToDoubleConverter extends TextToNumConverter<Double> {
    public static final Codec<TextToDoubleConverter> CODEC = createCodec(TextToDoubleConverter::new);
    private static final AlgebraOperationsStorage<Double> OPERATIONS_STORAGE = new AlgebraOperationsStorage<>(
            DoubleAddArgument::new,
            DoubleSubArgument::new,
            DoubleMulArgument::new,
            DoubleDivArgument::new,
            DoubleModArgument::new,
            DoublePowArgument::new,
            Double::parseDouble,
            DoubleGetParamOperation::new
    );

    public TextToDoubleConverter(String args) {
        super(parse(args));
    }

    private static Operation<Double> parse(String args) {
        List<Token> tokens = TextToNumConverter.parseTokens(args);
        return new AlgebraParser<>(tokens, OPERATIONS_STORAGE).parse();
    }
}
