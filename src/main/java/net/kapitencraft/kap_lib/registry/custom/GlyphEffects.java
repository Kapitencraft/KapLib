package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.ShakeEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.WaveEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import java.util.Map;
import java.util.stream.Collectors;

public interface GlyphEffects {
    DeferredRegister<GlyphEffect> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.GLYPH_EFFECTS);
    static Map<Character, GlyphEffect> effectsForKey() {
        return REGISTRY.getEntries().stream().map(Supplier::get).collect(Collectors.toMap(GlyphEffect::getKey, effect -> effect));
    }

    Supplier<RainbowEffect> RAINBOW = REGISTRY.register("rainbow", RainbowEffect::new);
    Supplier<WaveEffect> WAVE = REGISTRY.register("wave", WaveEffect::new);
    Supplier<ShakeEffect> SHAKE = REGISTRY.register("shake", ShakeEffect::new);
}
