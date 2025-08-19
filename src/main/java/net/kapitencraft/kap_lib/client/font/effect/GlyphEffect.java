package net.kapitencraft.kap_lib.client.font.effect;

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
}