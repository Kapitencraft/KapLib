package net.kapitencraft.kap_lib.component.font.effect;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.component.event.custom.RegisterGlyphEffectsEvent;
import net.kapitencraft.kap_lib.component.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.ShakeEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.WaveEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GlyphEffects {
    private static final Map<ResourceLocation, GlyphEffect> EFFECT_BY_NAME_MAP = new HashMap<>();
    private static final Map<GlyphEffect, ResourceLocation> NAME_BY_EFFECT_MAP = new HashMap<>();
    public static final Codec<GlyphEffect> CODEC = ResourceLocation.CODEC.xmap(EFFECT_BY_NAME_MAP::get, NAME_BY_EFFECT_MAP::get);

    public static final RainbowEffect RAINBOW = register("rainbow", RainbowEffect::new);
    public static final WaveEffect WAVE = register("wave", WaveEffect::new);
    public static final ShakeEffect SHAKE = register("shake", ShakeEffect::new);

    static {
        RegisterGlyphEffectsEvent event = new RegisterGlyphEffectsEvent(EFFECT_BY_NAME_MAP, NAME_BY_EFFECT_MAP);
        ModLoader.postEvent(event);
    }

    private static <T extends GlyphEffect> T register(String name, Supplier<T> provider) {
        T value = provider.get();
        ResourceLocation location = LibConstants.res(name);
        EFFECT_BY_NAME_MAP.put(location, value);
        NAME_BY_EFFECT_MAP.put(value, location);
        return value;
    }
}
