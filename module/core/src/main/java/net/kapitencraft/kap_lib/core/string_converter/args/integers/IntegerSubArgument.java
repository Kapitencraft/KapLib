package net.kapitencraft.kap_lib.core.string_converter.args.integers;

import net.kapitencraft.kap_lib.core.string_converter.args.Operation;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record IntegerSubArgument(Operation<Integer> left, Operation<Integer> right) implements Operation<Integer> {

    @Override
    public Integer value(ParamStorage<Integer> params) {
        return left.value(params) - right.value(params);
    }

    @Override
    public String literal() {
        return left.literal() + " - " + right.literal();
    }
}