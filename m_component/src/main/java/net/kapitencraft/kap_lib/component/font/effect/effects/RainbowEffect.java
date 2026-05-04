package net.kapitencraft.kap_lib.component.font.effect.effects;

import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;

/**
 * applies rainbow shader to glyphs
 */
public class RainbowEffect extends GlyphEffect {
    @Override
    public void apply(EffectSettings settings) {
        //dummy class as wrapper
        //I'd like to make it a ChatFormatting, but IDK how
    }

    @Override
    public char getKey() {
        return 'z';
    }
}
