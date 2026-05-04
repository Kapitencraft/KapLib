package net.kapitencraft.kap_lib.component.mixin.duck;

import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;

public interface IChromatic {
    RenderType getChromaType();

    void setChromaType(RenderType chromaType);

    static IChromatic of(BakedGlyph bakedGlyph) {
        return (IChromatic) bakedGlyph;
    }

    //TODO interface injection
    static IChromatic of(FontTexture fontTexture) {
        return (IChromatic) fontTexture;
    }
}
