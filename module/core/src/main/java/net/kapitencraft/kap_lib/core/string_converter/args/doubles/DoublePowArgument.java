package net.kapitencraft.kap_lib.core.string_converter.args.doubles;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record DoublePowArgument(Operation<Double> left, Operation<Double> right) implements Operation<Double> {

    @Override
    public Double value(ParamStorage<Double> params) {
        return Math.pow(left.value(params), right.value(params));
    }

    @Override
    public String literal() {
        return left.literal() + " ^ " + right.literal();
    }
}