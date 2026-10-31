package net.kapitencraft.kap_lib.component.font.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.component.ParamStorage;
import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * Glyph Effect that makes the Glyphs move up and down
 */
public class WaveEffect extends GlyphEffect<WaveEffect.WaveEffectInstance> {
    @Override
    public void apply(EffectSettings settings, WaveEffectInstance instance) {
        settings.y += Mth.sin((Util.getMillis() + settings.index * 90) * instance.scale) * instance.amplitude;
    }

    @Override
    public WaveEffectInstance createInstance(ParamStorage params) {
        String scale = params.getParam("scale", "s");
        String amplitude = params.getParam("amplitude", "a");
        return new WaveEffectInstance(
                scale != null ? Float.parseFloat(scale) : .01f,
                amplitude != null ? Float.parseFloat(amplitude) : 2
        );
    }

    @Override
    public MapCodec<WaveEffectInstance> codec() {
        return WaveEffectInstance.CODEC;
    }

    public record WaveEffectInstance(float scale, float amplitude) implements EffectInstance {
        private static final MapCodec<WaveEffectInstance> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                        Codec.FLOAT.optionalFieldOf("scale", .01f).forGetter(WaveEffectInstance::scale),
                        Codec.FLOAT.optionalFieldOf("amplitude", 2f).forGetter(WaveEffectInstance::amplitude)
                ).apply(i, WaveEffectInstance::new)
        );

        @Override
        public @NotNull GlyphEffect<?> getEffect() {
            return GlyphEffects.WAVE.get();
        }
    }
}
