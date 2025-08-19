package net.kapitencraft.kap_lib.client.font.effect.effects;

import net.kapitencraft.kap_lib.client.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.minecraft.Util;
import net.minecraft.util.Mth;

/**
 * Glyph Effect that makes the Glyphs move up and down
 */
public class WaveEffect extends GlyphEffect {
    @Override
    public void apply(EffectSettings settings) {
        settings.y += Mth.sin(Util.getMillis() * 0.01F + settings.index) * 2;
    }

    @Override
    public char getKey() {
        return 'w';
    }
}
