package net.kapitencraft.kap_lib.core.string_converter.converter;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

import java.util.function.Function;

public abstract class TextConverter<T> {
    protected static <T extends TextConverter<?>> Codec<T> createCodec(Function<String, T> constructor) {
        return Codec.STRING.xmap(constructor, TextConverter::toLiteral);
    }

    private final Operation<T> operation;

    protected TextConverter(Operation<T> operation) {
        this.operation = operation;
    }

    private String toLiteral() {
        return operation.literal();
    }

    @Override
    public String toString() {
        return "Converting: '" + operation + "'";
    }

    public T transfer(ParamStorage<T> storage) {
        return this.operation.value(storage);
    }
}