package net.kapitencraft.kap_lib.component.font.effect;

import net.minecraft.network.chat.Style;

/**
 * base class for the glyph effect
 */
public abstract class GlyphEffect {

    public GlyphEffect() {
    }

    /**
     * method to modify the {@link EffectSettings} for changing behaviour of the glyph rendering
     * @param settings the settings of the current glyph being applied
     */
    public abstract void apply(EffectSettings settings);

    /**
     * @return the unique char associated to this Effect
     * <br>mustn't match any {@link net.minecraft.ChatFormatting ChatFormatting} key
     */
    public abstract char getKey();



    /**
     * @param in the Style to add this effect to
     * @return the new style with applied effect
     */
    public final Style apply(Style in) {
        return EffectsStyle.of(in).addEffect(this);
    }
}