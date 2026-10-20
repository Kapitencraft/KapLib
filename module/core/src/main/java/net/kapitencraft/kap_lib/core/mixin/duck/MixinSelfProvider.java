package net.kapitencraft.kap_lib.core.mixin.duck;

public interface MixinSelfProvider<T> {

    default T self() {
        return (T) this;
    }
}
