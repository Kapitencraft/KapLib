package net.kapitencraft.kap_lib.core.string_converter.args;

import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;

public record BoolAndOperation(Operation<Boolean> left, Operation<Boolean> right) implements Operation<Boolean> {

    @Override
    public Boolean value(ParamStorage<Boolean> params) {
        return left.value(params) && right.value(params);
    }

    @Override
    public String literal() {
        return left.literal() + " && " + right.literal();
    }
}
