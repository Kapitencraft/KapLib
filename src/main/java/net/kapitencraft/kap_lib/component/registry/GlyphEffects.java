package net.kapitencraft.kap_lib.component.registry;

import net.kapitencraft.kap_lib.component.registry.custom.ComponentRegistries;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.ShakeEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.WaveEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import java.util.Map;
import java.util.stream.Collectors;

public interface GlyphEffects {
    DeferredRegister<GlyphEffect> REGISTRY = LibConstants.registry(ComponentRegistries.Keys.GLYPH_EFFECTS);
    static Map<Character, GlyphEffect> effectsForKey() {
        return REGISTRY.getEntries().stream().map(Supplier::get).collect(Collectors.toMap(GlyphEffect::getKey, effect -> effect));
    }

    Supplier<RainbowEffect> RAINBOW = REGISTRY.register("rainbow", RainbowEffect::new);
    Supplier<WaveEffect> WAVE = REGISTRY.register("wave", WaveEffect::new);
    Supplier<ShakeEffect> SHAKE = REGISTRY.register("shake", ShakeEffect::new);
}
