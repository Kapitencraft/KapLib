package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.mixin.duck.IChromatic;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(FontTexture.class)
public abstract class FontTextureMixin extends AbstractTexture implements IChromatic {
    private RenderType chromatic, seeThroughChromatic, polygonOffsetChromatic;

    @Override
    public void setChromaType(RenderType chromaType) {
        chromatic = chromaType;
    }

    @Override
    public RenderType getChromaType() {
        return chromatic;
    }
}
