package net.kapitencraft.kap_lib.component.mixin.duck;

import net.minecraft.client.renderer.RenderType;

public interface IChromatic {
    default RenderType getChromaType() {
        return null;
    }

    default void setChromaType(RenderType chromaType) {
    }
}