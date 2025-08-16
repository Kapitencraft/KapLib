package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadContents;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ComponentContents;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraComponentContents {
    DeferredRegister<Codec<? extends ComponentContents>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.COMPONENT_CONTENTS_TYPES);

    Holder<Codec<? extends ComponentContents>> PLAYER_HEAD = REGISTRY.register("player_head", () -> PlayerHeadContents.CODEC);
}
