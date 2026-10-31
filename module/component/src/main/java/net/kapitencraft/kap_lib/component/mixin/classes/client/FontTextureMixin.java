package net.kapitencraft.kap_lib.component.mixin.classes.client;

import net.kapitencraft.kap_lib.component.mixin.duck.GlyphTextureLookup;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(FontTexture.class)
public abstract class FontTextureMixin extends AbstractTexture implements GlyphTextureLookup {
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
