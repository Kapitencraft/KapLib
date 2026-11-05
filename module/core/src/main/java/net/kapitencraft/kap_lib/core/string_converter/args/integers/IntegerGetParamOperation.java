package net.kapitencraft.kap_lib.core.string_converter.args.integers;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record IntegerGetParamOperation(String paramName) implements Operation<Integer> {
    @Override
    public Integer value(ParamStorage<Integer> params) {
        return params.get(paramName);
    }

    @Override
    public String literal() {
        return paramName;
    }
}
