package net.kapitencraft.kap_lib.core.util;

import net.kapitencraft.kap_lib.core.LibConstants;
import org.jetbrains.annotations.Nullable;

/**
 * type of variable able to be used from inside lambda
 */
public class Reference<T> {

    private T value;

    private Reference(@Nullable T defaultValue) {
        value = defaultValue;
    }

    public Reference() {
    }

    public static <T> Reference<T> of(@Nullable T t) {
        return new Reference<>(t);
    }

    public Reference<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return value;
    }

    public int getIntValue() {
        if (value == null) {
            return 0;
        }
        try {
            return (int) value;
        } catch (Exception e) {
            LibConstants.LOGGER.warn("error whilst attempting to get value: {}", e.getMessage());
        }
        return 0;
    }
}
