package net.kapitencraft.kap_lib.component.event.custom;

import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterGlyphEffectsEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, GlyphEffect> effectByNameMap;
    private final Map<GlyphEffect, ResourceLocation> nameByEffectMap;

    public RegisterGlyphEffectsEvent(Map<ResourceLocation, GlyphEffect> effectByNameMap, Map<GlyphEffect, ResourceLocation> nameByEffectMap) {
        this.effectByNameMap = effectByNameMap;
        this.nameByEffectMap = nameByEffectMap;
    }

    public void register(ResourceLocation location, GlyphEffect effect) {
        if (effectByNameMap.putIfAbsent(location, effect) != null) {
            throw new IllegalArgumentException("duplicate glyph effect with ID " + location);
        }
        nameByEffectMap.put(effect, location);
    }
}
