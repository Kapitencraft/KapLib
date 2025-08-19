package net.kapitencraft.kap_lib.client.font.effect;

import net.minecraft.network.chat.Style;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * interface (or <i>duck-class</i> into {@link Style}) for {@link GlyphEffect}s
 */
public interface EffectsStyle {

    /**
     * adds an effect to the {@link Style}
     * @param effect the effect to add
     */
    Style addEffect(GlyphEffect effect);

    void setEffects(GlyphEffect[] s);


    /**
     * @return all effects applied
     */
    @Pure
    GlyphEffect @NotNull [] getEffects();

    /**
     * converts (or casts) a {@code  Style} to an {@code EffectsStyle} to use above methods
     * @param style the style to convert
     * @return the {@code EffectsStyle} that the Style contains
     * @apiNote use {@link net.kapitencraft.kap_lib.helpers.MiscHelper#withSpecial(Style, GlyphEffect) MiscHelper#withSpecial} instead
     */
    @ApiStatus.Internal
    static EffectsStyle of(Style style) {
        return (EffectsStyle) style;
    }

    default boolean hasEffect(GlyphEffect effect) {
        for (GlyphEffect effect1 : this.getEffects()) {
            if (effect == effect1) return true;
        }
        return false;
    }

    default boolean hasEffects() {
        return this.getEffects().length > 0;
    }
}
