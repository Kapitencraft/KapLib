package net.kapitencraft.kap_lib.component.font.effect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * class to keep all information for the glyph color, a, x and y
 * <br> as well if it's just a shadow glyph, and it's index (of the rendered text)
 */
public class EffectSettings {
    public float r, g, b, a, x, y;
    public boolean isShadow = false;
    public int index;
    public ResourceLocation texture;
    public @Nullable RenderType renderType;
}