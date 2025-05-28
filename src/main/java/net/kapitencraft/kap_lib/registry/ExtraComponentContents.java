package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadContents;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.ComponentContents;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraComponentContents {
    DeferredRegister<Codec<? extends ComponentContents>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.COMPONENT_CONTENTS_TYPES);

    RegistryObject<Codec<PlayerHeadContents>> PLAYER_HEAD = REGISTRY.register("player_head", () -> PlayerHeadContents.CODEC);
}
