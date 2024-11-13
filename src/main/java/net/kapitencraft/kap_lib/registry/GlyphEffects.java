package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.ShakeEffect;
import net.kapitencraft.kap_lib.client.font.effect.effects.WaveEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ModRegistryKeys;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.stream.Collectors;

public interface GlyphEffects {
    DeferredRegister<GlyphEffect> REGISTRY = KapLibMod.registry(ModRegistryKeys.GLYPH_EFFECTS);
    static Map<Character, GlyphEffect> effectsForKey() {
        return REGISTRY.getEntries().stream().map(RegistryObject::get).collect(Collectors.toMap(GlyphEffect::getKey, effect -> effect));
    }

    RegistryObject<RainbowEffect> RAINBOW = REGISTRY.register("rainbow", RainbowEffect::new);
    RegistryObject<WaveEffect> WAVE = REGISTRY.register("wave", WaveEffect::new);
    RegistryObject<ShakeEffect> SHAKE = REGISTRY.register("shake", ShakeEffect::new);
}
