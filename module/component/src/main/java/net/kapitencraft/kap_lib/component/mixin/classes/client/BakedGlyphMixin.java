package net.kapitencraft.kap_lib.component.mixin.classes.client;

import net.kapitencraft.kap_lib.component.mixin.duck.GlyphTextureLookup;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedGlyph.class)
public class BakedGlyphMixin implements GlyphTextureLookup {
    @Unique
    private ResourceLocation texture;

    @Override
    public void setTextureLocation(ResourceLocation location) {
        texture = location;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return texture;
    }
}
