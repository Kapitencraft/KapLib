package net.kapitencraft.kap_lib.core.string_converter.args.doubles;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record DoubleGetParamOperation(String paramName) implements Operation<Double> {
    @Override
    public Double value(ParamStorage<Double> params) {
        return params.get(paramName);
    }

    @Override
    public String literal() {
        return paramName;
    }
}
