package net.kapitencraft.kap_lib.client;

import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadContents;
import net.minecraft.network.chat.MutableComponent;

import java.util.UUID;

public interface ExtraComponents {

    /**
     * creates a new component that shows the player head of the given player a
     */
    static MutableComponent playerHead(UUID player) {
        return MutableComponent.create(new PlayerHeadContents(player));
    }
}
