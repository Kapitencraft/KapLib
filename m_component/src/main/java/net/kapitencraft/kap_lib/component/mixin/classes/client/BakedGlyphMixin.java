package net.kapitencraft.kap_lib.component.mixin.classes.client;

import net.kapitencraft.kap_lib.component.mixin.duck.IChromatic;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedGlyph.class)
public class BakedGlyphMixin implements IChromatic {
    @Unique
    private RenderType chromatic;

    @Override
    public RenderType getChromaType() {
        return chromatic;
    }

    @Override
    public void setChromaType(RenderType chromaType) {
        chromatic = chromaType;
    }
}
