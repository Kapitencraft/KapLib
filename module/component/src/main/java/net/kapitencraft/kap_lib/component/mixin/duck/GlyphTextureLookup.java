package net.kapitencraft.kap_lib.component.mixin.duck;

import net.minecraft.resources.ResourceLocation;

public interface GlyphTextureLookup {
    default ResourceLocation getTextureLocation() {
        throw new AssertionError();
    }

    default void setTextureLocation(ResourceLocation location) {
        throw new AssertionError();
    }
}