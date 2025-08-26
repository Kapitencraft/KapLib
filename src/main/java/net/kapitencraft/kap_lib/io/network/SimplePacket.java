package net.kapitencraft.kap_lib.io.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface SimplePacket {

    void toBytes(FriendlyByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> sup);
}
