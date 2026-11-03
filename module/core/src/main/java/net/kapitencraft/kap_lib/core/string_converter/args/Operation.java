package net.kapitencraft.kap_lib.core.string_converter.args;

import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public interface Operation<T> {
    T value(ParamStorage<T> params);

    String literal();
}