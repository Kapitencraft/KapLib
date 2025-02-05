package net.kapitencraft.kap_lib.client.font.effect;

import net.minecraft.network.chat.Style;

import java.util.List;

/**
 * interface (or <i>duck-class</i> into {@link Style}) for {@link GlyphEffect}s
 */
public interface EffectsStyle {

    /**
     * adds an effect to the {@link Style}
     * @param effect the effect to add
     */
    Style addEffect(GlyphEffect effect);

    /**
     * @return all effects applied
     */
    List<GlyphEffect> getEffects();

    /**
     * converts (or casts) a {@code  Style} to an {@code EffectsStyle} to use above methods
     * @param style the style to convert
     * @return the {@code EffectsStyle} that the Style contains
     */
    static EffectsStyle of(Style style) {
        return (EffectsStyle) style;
    }
}
