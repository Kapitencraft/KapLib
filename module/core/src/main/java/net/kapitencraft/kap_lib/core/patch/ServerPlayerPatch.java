package net.kapitencraft.kap_lib.core.patch;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;

public interface ServerPlayerPatch {

    /**
     * sets the subtitle to the given Component on the given player
     */
    default void sendSubTitle(Component subtitle) {
        ((ServerPlayer) this).connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
    }

    /**
     * sends the given Component as the title for the given player
     */
    default void sendTitle(Component title) {
        ((ServerPlayer) this).connection.send(new ClientboundSetTitleTextPacket(title));
    }

    /**
     * removes the active title from the given player
     */
    default void clearTitle() {
        ((ServerPlayer) this).connection.send(new ClientboundClearTitlesPacket(true));
    }
}
