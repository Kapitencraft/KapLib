package net.kapitencraft.kap_lib.component.font.effect;

import net.minecraft.network.chat.Style;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * interface (or <i>duck-class</i> into {@link Style}) for {@link GlyphEffect}s
 */
public interface EffectsStyle {

    /**
     * adds an effect to the {@link Style}
     * @param effect the effect to add
     */
    Style addEffect(GlyphEffect.EffectInstance effect);

    Style removeEffect(GlyphEffect<?> effect);

    void setEffects(GlyphEffect.EffectInstance[] s);

    /**
     * @return all effects applied
     */
    @Pure
    GlyphEffect.EffectInstance @NotNull [] getEffects();

    default boolean hasEffect(Supplier<? extends GlyphEffect<?>> effect) {
        return hasEffect(effect.get());
    }

    default boolean hasEffect(GlyphEffect<?> effect) {
        for (GlyphEffect.EffectInstance effect1 : this.getEffects()) {
            if (effect == effect1.getEffect()) return true;
        }
        return false;
    }

    default boolean hasEffects() {
        return this.getEffects().length > 0;
    }

}
