package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadContents;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ComponentContents;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraComponentContents {
    DeferredRegister<ComponentContents.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.COMPONENT_CONTENTS_TYPES);

    Holder<ComponentContents.Type<?>> PLAYER_HEAD = REGISTRY.register("player_head", () -> PlayerHeadContents.TYPE);
}
