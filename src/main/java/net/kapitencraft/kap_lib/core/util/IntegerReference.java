package net.kapitencraft.kap_lib.core.util;

import com.mojang.serialization.Codec;

public class IntegerReference extends Reference<Integer> {
    public static final Codec<IntegerReference> CODEC = Codec.INT.xmap(IntegerReference::create, IntegerReference::getIntValue);

    public void increase() {
        this.setValue(this.getIntValue() + 1);
    }

    public void decrease() {
        this.setValue(this.getIntValue() - 1);
    }

    @Override
    public IntegerReference setValue(Integer value) {
        return (IntegerReference) super.setValue(value);
    }

    public static IntegerReference create(int value) {
        return new IntegerReference().setValue(value);
    }
}
