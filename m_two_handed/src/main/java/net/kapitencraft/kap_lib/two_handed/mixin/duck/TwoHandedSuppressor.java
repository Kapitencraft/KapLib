package net.kapitencraft.kap_lib.two_handed.mixin.duck;

import net.minecraft.resources.ResourceLocation;

public interface TwoHandedSuppressor {
    /**
     * @return whether items shouldn't be considered two-handed, even when having the data component
     */
    default boolean kap_lib$suppressesTwoHanded() {
        return false;
    }

    default void kap_lib$addSuppressionFlag(ResourceLocation location) {}

    default void kap_lib$removeSuppressionFlag(ResourceLocation location) {}

    default boolean kap_lib$hasSuppressionFlag(ResourceLocation key) {
        return false;
    }
}