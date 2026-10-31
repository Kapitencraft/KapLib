package net.kapitencraft.kap_lib.component.font.effect;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.component.ComponentRegistries;
import net.kapitencraft.kap_lib.component.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.ShakeEffect;
import net.kapitencraft.kap_lib.component.font.effect.effects.WaveEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface GlyphEffects {
    DeferredRegister<GlyphEffect<?>> REGISTRY = LibConstants.registry(ComponentRegistries.Keys.GLYPH_EFFECTS);
    Codec<GlyphEffect.EffectInstance> CODEC = ComponentRegistries.GLYPH_EFFECTS.byNameCodec().dispatch(GlyphEffect.EffectInstance::getEffect, GlyphEffect::codec);

    Supplier<RainbowEffect> RAINBOW = register("rainbow", RainbowEffect::new);
    Supplier<WaveEffect> WAVE = register("wave", WaveEffect::new);
    Supplier<ShakeEffect> SHAKE = register("shake", ShakeEffect::new);

    private static <T extends GlyphEffect<?>> Supplier<T> register(String name, Supplier<T> provider) {
        return REGISTRY.register(name, provider);
    }

    static @Nullable ResourceLocation getKey(@NotNull GlyphEffect<?> effect) {
        return ComponentRegistries.GLYPH_EFFECTS.getKey(effect);
    }
}
