package net.kapitencraft.kap_lib.core.string_converter.args.doubles;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record LiteralValueOperation(Double value) implements Operation<Double> {
    @Override
    public Double value(ParamStorage<Double> params) {
        return value;
    }

    @Override
    public String literal() {
        return value.toString();
    }
}
