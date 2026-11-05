package net.kapitencraft.kap_lib.core.string_converter.args;

import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record LiteralValueOperation<T>(T value) implements Operation<T> {
    @Override
    public T value(ParamStorage<T> params) {
        return value;
    }

    @Override
    public String literal() {
        return value.toString();
    }
}
