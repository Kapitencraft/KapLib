package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.AlgebraOperationsStorage;
import net.kapitencraft.kap_lib.core.string_converter.AlgebraParser;
import net.kapitencraft.kap_lib.core.string_converter.Token;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.args.integers.*;

import java.util.List;

public class TextToIntConverter extends TextToNumConverter<Integer> {
    public static final Codec<TextToIntConverter> CODEC = createCodec(TextToIntConverter::new);
    private static final AlgebraOperationsStorage<Integer> OPERATIONS_STORAGE = new AlgebraOperationsStorage<>(
            IntegerAddArgument::new,
            IntegerSubArgument::new,
            IntegerMulArgument::new,
            IntegerDivArgument::new,
            IntegerModArgument::new,
            IntegerPowArgument::new,
            Integer::parseInt,
            IntegerGetParamOperation::new
    );

    public TextToIntConverter(String args) {
        super(parse(args));
    }

    private static Operation<Integer> parse(String args) {
        List<Token> tokens = TextToNumConverter.parseTokens(args);
        return new AlgebraParser<>(tokens, OPERATIONS_STORAGE).parse();
    }
}