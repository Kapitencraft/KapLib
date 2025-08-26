package net.kapitencraft.kap_lib.io.network.request;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface IRequestable<D, I> {
    void writeToNetwork(D target, FriendlyByteBuf buf);

    D getFromNetwork(FriendlyByteBuf buf);

    void writeRequest(I target, FriendlyByteBuf buf);

    I readRequest(FriendlyByteBuf buf);

    /**
     * used to pack the request ({@link I}) to the requested data ({@link D})
     * @param source value the request send to the server to pack into {@link D} and send back to the client
     * @param player the player that send the request
     * @return the {@link D} packed and ready to be sent back to the client
     */
    D pack(I source, ServerPlayer player);
}
