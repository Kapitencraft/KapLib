package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public class TextToBoolConverter extends TextConverter<Boolean> {
    public static final Codec<TextToBoolConverter> CODEC = createCodec(TextToBoolConverter::new);

    protected TextToBoolConverter(String args) {
        super(new Operation<Boolean>() {
            @Override
            public Boolean value(ParamStorage<Boolean> params) {
                return true;
            }

            @Override
            public String literal() {
                return "true";
            }
        });
    }
}
