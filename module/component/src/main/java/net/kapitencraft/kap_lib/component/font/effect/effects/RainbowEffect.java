package net.kapitencraft.kap_lib.component.font.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.component.ParamStorage;
import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import net.kapitencraft.kap_lib.shader.ShaderRenderTypes;
import net.kapitencraft.kap_lib.shader.chroma.ChromaOrigin;
import net.kapitencraft.kap_lib.shader.chroma.ChromaType;
import org.jetbrains.annotations.NotNull;

/**
 * applies rainbow shader to glyphs
 */
public class RainbowEffect extends GlyphEffect<RainbowEffect.RainbowEffectInstance> {

    @Override
    public void apply(EffectSettings settings, RainbowEffectInstance instance) {
        if (!settings.isShadow)
            settings.renderType = ShaderRenderTypes.chromatic(settings.texture, instance.speed, instance.type, instance.spacing, instance.origin);
    }

    @Override
    public RainbowEffectInstance createInstance(ParamStorage params) {
        String origin = params.getParam("origin", "o");
        String spacing = params.getParam("spacing", "sg");
        String speed = params.getParam("speed", "sd");
        String type = params.getParam("type", "t");

        return new RainbowEffectInstance(
                origin != null ? ChromaOrigin.CODEC.byName(origin, ChromaOrigin.BOTTOM_RIGHT) : ChromaOrigin.BOTTOM_RIGHT,
                spacing != null ? Float.parseFloat(spacing) : 2f,
                speed != null ? Float.parseFloat(speed) : 4,
                type != null ? ChromaType.CODEC.byName(type, ChromaType.LINEAR) : ChromaType.LINEAR
        );
    }

    @Override
    public MapCodec<RainbowEffectInstance> codec() {
        return RainbowEffectInstance.CODEC;
    }

    public record RainbowEffectInstance(ChromaOrigin origin, float spacing, float speed, ChromaType type) implements GlyphEffect.EffectInstance {
        public static final MapCodec<RainbowEffectInstance> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ChromaOrigin.CODEC.optionalFieldOf("origin", ChromaOrigin.BOTTOM_RIGHT).forGetter(RainbowEffectInstance::origin),
                Codec.FLOAT.optionalFieldOf("spacing", 2f).forGetter(RainbowEffectInstance::spacing),
                Codec.FLOAT.optionalFieldOf("speed", 4f).forGetter(RainbowEffectInstance::speed),
                ChromaType.CODEC.optionalFieldOf("type", ChromaType.LINEAR).forGetter(RainbowEffectInstance::type)
        ).apply(i, RainbowEffectInstance::new));

        @Override
        public @NotNull GlyphEffect<?> getEffect() {
            return GlyphEffects.RAINBOW.get();
        }
    }
}
