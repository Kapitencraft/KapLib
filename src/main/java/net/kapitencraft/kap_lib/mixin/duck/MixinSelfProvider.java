package net.kapitencraft.kap_lib.mixin.duck;

public interface MixinSelfProvider<T> {

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
