package net.kapitencraft.kap_lib.two_handed.mixin.duck;

public interface TwoHandedSuppressor {
    /**
     *
     * @return whether items shouldn't be considered two-handed, even when having the data component
     */
    boolean suppressesTwoHanded();

    void setSuppressed(boolean suppressed);
}