package net.kapitencraft.kap_lib.util;

public class IntegerReference extends Reference<Integer> {

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
