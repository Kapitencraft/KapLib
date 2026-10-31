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
 * Glyph effect that makes the glyphs move from left to right
 */
public class ShakeEffect extends GlyphEffect<ShakeEffect.ShakeEffectInstance> {
    @Override
    public void apply(EffectSettings settings, ShakeEffectInstance instance) {
        settings.x += Mth.sin(Util.getMillis() * instance.scale + settings.index) * instance.amplitude;
    }

    @Override
    public ShakeEffectInstance createInstance(ParamStorage params) {
        String scale = params.getParam("scale", "s");
        String amplitude = params.getParam("amplitude", "a");
        return new ShakeEffectInstance(
                scale != null ? Float.parseFloat(scale) : .01f,
                amplitude != null ? Float.parseFloat(amplitude) : 2
        );
    }

    @Override
    public MapCodec<ShakeEffectInstance> codec() {
        return ShakeEffectInstance.CODEC;
    }

    public record ShakeEffectInstance(float scale, float amplitude) implements EffectInstance {
        private static final MapCodec<ShakeEffectInstance> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.FLOAT.optionalFieldOf("scale", .01f).forGetter(ShakeEffectInstance::scale),
                Codec.FLOAT.optionalFieldOf("amplitude", 2f).forGetter(ShakeEffectInstance::amplitude)
        ).apply(i, ShakeEffectInstance::new));

        @Override
        public @NotNull GlyphEffect<?> getEffect() {
            return GlyphEffects.SHAKE.get();
        }
    }
}
