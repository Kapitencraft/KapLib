package net.kapitencraft.kap_lib.component.font.effect;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.component.ParamStorage;
import org.jetbrains.annotations.NotNull;

/**
 * base class for the glyph effect
 */
public abstract class GlyphEffect<T extends GlyphEffect.EffectInstance> {

    protected GlyphEffect() {
    }

    /**
     * method to modify the {@link EffectSettings} for changing behaviour of the glyph rendering
     *
     * @param settings the settings of the current glyph being applied
     * @param instance the settings of the effect to be processed
     */
    public abstract void apply(EffectSettings settings, T instance);

    public abstract T createInstance(ParamStorage params);

    public abstract MapCodec<T> codec();

    public interface EffectInstance {
        @NotNull GlyphEffect<?> getEffect();

        @SuppressWarnings("unchecked")
        default <T extends GlyphEffect.EffectInstance> void apply(EffectSettings settings) {
            ((GlyphEffect<T>) this.getEffect()).apply(settings, (T) this);
        }
    }
}