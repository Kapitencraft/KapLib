package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.Holder;

public interface Overlays {
    DeferredRegister<OverlayProperties> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.OVERLAY_PROPERTIES);

    Holder<OverlayProperties> STATS = REGISTRY.register("stats", () -> new OverlayProperties(-188.75f, 24f, .75f, .75f, OverlayProperties.Alignment.MIDDLE, OverlayProperties.Alignment.BOTTOM_RIGHT));
    Holder<OverlayProperties> MANA = REGISTRY.register("mana", () -> new OverlayProperties(2f, 2, 1, 1, OverlayProperties.Alignment.TOP_LEFT, OverlayProperties.Alignment.TOP_LEFT).setVisible(false));
}
