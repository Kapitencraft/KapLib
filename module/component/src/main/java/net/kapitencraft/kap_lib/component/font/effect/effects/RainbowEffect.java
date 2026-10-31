package net.kapitencraft.kap_lib.component.font.effect.effects;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.component.ParamStorage;
import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import org.jetbrains.annotations.NotNull;

/**
 * applies rainbow shader to glyphs
 */
public class RainbowEffect extends GlyphEffect<RainbowEffect.RainbowEffectInstance> {

    @Override
    public void apply(EffectSettings settings, RainbowEffectInstance instance) {
        //TODO find a way to wire the effect all the way to the shader
        //dummy class as wrapper
        //I'd like to make it a ChatFormatting, but IDK how
    }

    @Override
    public RainbowEffectInstance createInstance(ParamStorage params) {
        return new RainbowEffectInstance();
    }

    @Override
    public MapCodec<RainbowEffectInstance> codec() {
        return RainbowEffectInstance.CODEC;
    }

    public record RainbowEffectInstance() implements GlyphEffect.EffectInstance {
        public static final MapCodec<RainbowEffectInstance> CODEC = MapCodec.unit(RainbowEffectInstance::new);

        @Override
        public @NotNull GlyphEffect<?> getEffect() {
            return GlyphEffects.RAINBOW.get();
        }
    }
}
